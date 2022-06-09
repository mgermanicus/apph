package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderRequest;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.PhotosRequest;
import com.viseo.apph.exception.MaxSizeExceededException;
import com.viseo.apph.dto.MessageResponse;
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

    @Value("${max-zip-size-mb}")
    public long zipMaxSize;

    @Autowired
    S3Dao s3Dao;

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
            } else if (moveToFolder.getParentFolderId() != null && moveToFolder.getParentFolderId().equals(request.getFolderIdToBeMoved())) {
                logger.error("Folder parent can not be moved to it's child folder");
                throw new UnauthorizedException("folder.error.moveFolder");
            } else {
                return moveFolder(toBeMoved, request.getDestinationFolderId());
            }
        }
    }

    MessageResponse moveFolder(Folder folderToBeMoved, Long toMoveToFolder) {
        List<Folder> folders = folderDao.getFoldersByParentId(toMoveToFolder);
        for (Folder folder : folders) {
            if (folder.getName().equals(folderToBeMoved.getName())) {
                List<Photo> photosToBeMoved = photoDao.getPhotosByFolder(folderToBeMoved);
                List<Folder> foldersToBeMoved = folderDao.getFoldersByParentId(folderToBeMoved.getId());
                for (Photo p : photosToBeMoved) {
                    p.setFolder(folder);
                }
                for (Folder f : foldersToBeMoved) {
                    f.setParentFolderId(folder.getId());
                }
                return new MessageResponse("success: folder.successMove");
            }
        }
        folderToBeMoved.setParentFolderId(toMoveToFolder);
        return new MessageResponse("success: folder.successMove");
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

    public FolderResponse download(User user, PhotosRequest photosRequest) throws UnauthorizedException, IOException, MaxSizeExceededException {
        long[] ids = photosRequest.getIds();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        for (Long id : ids) {
            Folder folder = folderDao.getFolderById(id);
            if (folder == null) {
                throw new FileNotFoundException();
            }
            if (user.getId() != folder.getUser().getId()) {
                throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
            }
            zipSubFolder(bos, zipOut, folder, "");
        }
        zipOut.closeEntry();
        zipOut.close();
        return new FolderResponse().setData(bos.toByteArray()).setName("APPH-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())).setFormat(".zip");
    }

    void zipSubFolder(ByteArrayOutputStream bos, ZipOutputStream zipOut, Folder folder, String parentFolders) throws IOException, MaxSizeExceededException {
        String prefix = parentFolders + folder.getName() + "/";
        zipOut.putNextEntry(new ZipEntry(prefix));
        Set<String> names = new HashSet<>();
        List<Photo> photoList = photoDao.getPhotosByFolder(folder);
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
                    throw new MaxSizeExceededException("Erreur: Taille maximale du ZIP dépassée.");
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
}
