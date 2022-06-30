package com.viseo.apph.service;

import com.viseo.apph.dao.*;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderRequest;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.exception.MaxSizeExceededException;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FolderService {
    Logger logger = LoggerFactory.getLogger(FolderService.class);

    @Autowired
    FolderDao folderDao;

    @Autowired
    UserDao userDao;

    @Autowired
    PhotoDao photoDao;

    @Autowired
    S3Dao s3Dao;

    @Autowired
    SettingDao settingDao;

    @Transactional
    public FolderResponse getFoldersByUser(User user) throws NotFoundException {
        List<Folder> folderList = folderDao.getFolderByUser(user.getId());
        Folder parentFolder = getParentFolder(folderList);
        FolderResponse parentFolderResponse = new FolderResponse()
                .setId(parentFolder.getId())
                .setVersion(parentFolder.getVersion())
                .setName(parentFolder.getName())
                .setParentFolderId(parentFolder.getParentFolderId());
        return connectFolderToChildrenFolder(parentFolderResponse, folderList);
    }

    @Transactional
    public FolderResponse getFoldersByParentId(long parentId, User user) throws IllegalArgumentException {
        List<Folder> folderList = new ArrayList<>();
        Folder parentFolder;
        if (parentId != -1) {
            folderList = folderDao.getFoldersByParentId(parentId);
            parentFolder = folderDao.getFolderById(parentId);
        } else {
            parentFolder = folderDao.getParentFolderByUser(user);
        }
        if(folderList!= null && parentFolder != null) {
            FolderResponse parentFolderResponse = new FolderResponse()
                    .setId(parentFolder.getId())
                    .setVersion(parentFolder.getVersion())
                    .setName(parentFolder.getName())
                    .setParentFolderId(parentFolder.getParentFolderId());
            return connectFolderToChildrenFolder(parentFolderResponse, folderList);
        }
        throw new IllegalArgumentException("request.error.illegalArgument");
    }

    @Transactional
    public FolderResponse createFolder(User user, FolderRequest request) throws NotFoundException, UnauthorizedException {
        if (request.getParentFolderId() == null) {
            logger.error("Cannot create a root folder.");
            throw new UnauthorizedException("folder.error.root");
        }
        if (request.getName().length() > 255) {
            logger.error("The folder name cannot exceed 255 characters.");
            throw new IllegalArgumentException("folder.error.folderNameOverChar");
        }
        Folder parentFolder = folderDao.getFolderById(request.getParentFolderId());
        if (parentFolder == null) {
            logger.error("Parent folder not found.");
            throw new NotFoundException("folder.error.notFound");
        }
        if (parentFolder.getUser().getId() != user.getId()) {
            logger.error("User doesn't have access to this folder.");
            throw new UnauthorizedException("folder.error.unauthorized");
        }
        Folder folder = new Folder().setName(request.getName()).setParentFolderId(request.getParentFolderId()).setUser(user);
        folderDao.createFolder(folder);
        return getFoldersByUser(user);
    }

    @Transactional
    public MessageResponse moveFolder(User user, FolderRequest request) throws UnauthorizedException, NotFoundException {
        if (request.getFolderIdToBeMoved() == null || request.getDestinationFolderId() == null) {
            logger.error("Folder to be moved can not be null");
            throw new UnauthorizedException("folder.error.moveFolder");
        } else {
            Folder toBeMoved = folderDao.getFolderById(request.getFolderIdToBeMoved());
            Folder moveToFolder = folderDao.getFolderById(request.getDestinationFolderId());
            if (toBeMoved == null || moveToFolder == null) {
                logger.error("Folder not found.");
                throw new NotFoundException("folder.error.nullFolder");
            } else if (folderDao.getParentFolderByUser(user).getId() == request.getFolderIdToBeMoved()) {
                logger.error("Root folder can not be moved");
                throw new UnauthorizedException("folder.error.moveFolder");
            } else if (!isMovableFolder(user, toBeMoved, moveToFolder)) {
                logger.error("Folder parent can not be moved to it's child folder");
                throw new UnauthorizedException("folder.error.moveFolder");
            } else {
                moveFolder(toBeMoved, request.getDestinationFolderId());
                return new MessageResponse("success: folder.successMove");
            }
        }
    }

    @Transactional
    public MessageResponse deleteFolder(User user, FolderRequest request) throws UnauthorizedException, NotFoundException {
        Folder srcFolder = folderDao.getFolderById(request.getId());
        if (srcFolder == null) {
            logger.error("Folder not found.");
            throw new NotFoundException("folder.error.notExist");
        }
        if(srcFolder.getUser().getId() != user.getId()) {
            logger.error("The user doesn't have access to this folder.");
            throw new UnauthorizedException("folder.error.unauthorized");
        }
        if (srcFolder.getParentFolderId() == null) {
            logger.error("Root Folder cannot be remove.");
            throw new UnauthorizedException("folder.error.deleteFolder");
        }
        if (request.getDestinationFolderId() != null) {
            Folder dstFolder = folderDao.getFolderById(request.getDestinationFolderId());
            if (dstFolder == null) {
                logger.error("Folder not found.");
                throw new NotFoundException("folder.error.notExist");
            }
            if (!isMovableFolder(user, srcFolder, dstFolder)) {
                logger.error("The folder cannot be moved to the designated folder.");
                throw new UnauthorizedException("folder.error.moveFolder");
            }
            List<Folder> srcFolders = folderDao.getFoldersByParentId(request.getId());
            moveFolderPhotos(srcFolder, dstFolder);
            for (Folder folder : srcFolders) {
                moveFolder(folder, dstFolder.getId());
            }
        }
        deleteFolder(srcFolder);
        return new MessageResponse("folder.successDelete");
    }

    void moveFolder(Folder folderToBeMoved, Long toMoveToFolder) {
        List<Folder> folders = folderDao.getFoldersByParentId(toMoveToFolder);
        for (Folder folder : folders) {
            if (folder.getName().equals(folderToBeMoved.getName())) {
                moveFolderPhotos(folderToBeMoved, folder);
                List<Folder> foldersToBeMoved = folderDao.getFoldersByParentId(folderToBeMoved.getId());
                for (Folder f : foldersToBeMoved) {
                    moveFolder(f, folder.getId());
                }
                folderDao.delete(folderToBeMoved);
                return;
            }
        }
        folderToBeMoved.setParentFolderId(toMoveToFolder);
    }

    void moveFolderPhotos(Folder srcFolder, Folder dstFolder) {
        List<Photo> srcPhotos = new ArrayList<>(srcFolder.getPhotos());
        for (Photo photo : srcPhotos) {
            int count = 0;
            while (photoDao.existNameInFolder(dstFolder, count == 0 ? photo.getTitle() : photo.getTitle() + "_" + count, photo.getFormat())) {
                count++;
            }
            if (count != 0) {
                photo.setTitle(photo.getTitle() + "_" + count);
            }
            photo.setFolder(dstFolder);
        }
    }

    void deleteFolder(Folder folder) {
        List<Folder> childFolders = folderDao.getFoldersByParentId(folder.getId());
        childFolders.forEach(this::deleteFolder);
        folderDao.delete(folder);
    }

    boolean isMovableFolder(User user, Folder srcFolder, Folder dstFolder) {
        if (dstFolder.getParentFolderId() == null)
            return true;
        Map<Long, Long> folderStructure = folderDao.getFolderParentChildStructureByUser(user);
        Long dstId = dstFolder.getId();
        while (dstId != -1 && dstId != srcFolder.getId()) {
            dstId = folderStructure.get(dstId);
        }
        return dstId != srcFolder.getId();
    }

    Folder getParentFolder(List<Folder> folders) throws NotFoundException {
        for (Folder folder : folders) {
            if (folder.getParentFolderId() == null) {
                folders.remove(folder);
                return folder;
            }
        }
        logger.error("Parent folder not found.");
        throw new NotFoundException("folder.error.notFound");
    }

    FolderResponse connectFolderToChildrenFolder(FolderResponse parentFolder, List<Folder> folders) {
        List<Folder> foldersCopy = new ArrayList<>(folders);
        for (Folder folder : folders) {
            if (parentFolder.getId() == folder.getParentFolderId()) {
                FolderResponse childFolderResponse = new FolderResponse()
                        .setId(folder.getId())
                        .setVersion(folder.getVersion())
                        .setName(folder.getName())
                        .setParentFolderId(folder.getParentFolderId());
                foldersCopy.remove(folder);
                parentFolder.addChildFolder(connectFolderToChildrenFolder(childFolderResponse, foldersCopy));
            }
        }
        return parentFolder;
    }

    public FolderResponse downloadFolder(User user, FolderRequest folderRequest) throws UnauthorizedException, IOException, MaxSizeExceededException {
        long id = folderRequest.getId();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        Folder folder = folderDao.getFolderById(id);
        if (folder == null) {
            throw new FileNotFoundException();
        }
        if (user.getId() != folder.getUser().getId()) {
            throw new UnauthorizedException("request.error.unauthorizedResource");
        }
        zipSubFolder(bos, zipOut, folder, "");
        zipOut.closeEntry();
        zipOut.close();
        return new FolderResponse().setData(bos.toByteArray()).setName("APPH-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    void zipSubFolder(ByteArrayOutputStream bos, ZipOutputStream zipOut, Folder folder, String parentFolders) throws IOException, MaxSizeExceededException {
        String prefix = parentFolders + folder.getName() + "/";
        zipOut.putNextEntry(new ZipEntry(prefix));
        Set<String> names = new HashSet<>();
        List<Photo> photoList = photoDao.getPhotosByFolder(folder);
        long zipMaxSize = settingDao.getSetting().getDownloadSize();
        if (photoList != null) {
            for (Photo photo : photoList) {
                byte[] photoByte = s3Dao.download(photo);
                String name = photo.getTitle() + photo.getFormat();
                for (int i = 1; !names.add(name); i++)
                    name = photo.getTitle() + "_" + i + photo.getFormat();
                ZipEntry zipEntry = new ZipEntry(prefix + name);
                zipEntry.setSize(photoByte.length);
                zipOut.putNextEntry(zipEntry);
                zipOut.write(photoByte);
                if (bos.size() > zipMaxSize * 1024 * 1024) {
                    throw new MaxSizeExceededException("download.error.oversize");
                }
            }
        }
        List<Folder> folderList = folderDao.getFoldersByParentId(folder.getId());
        if (folderList != null) {
            for (Folder subfolder : folderList) {
                zipSubFolder(bos, zipOut, subfolder, prefix);
            }
        }
    }

    @Transactional
    public Map<Long, Long> test(User user) {
        return folderDao.getFolderParentChildStructureByUser(user);
    }
}
