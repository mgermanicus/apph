package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderRequest;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {
    Logger logger = LoggerFactory.getLogger(FolderService.class);

    @Autowired
    FolderDao folderDao;

    @Autowired
    UserDao userDao;

    @Autowired
    PhotoDao photoDao;

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
    public FolderResponse getFoldersByParentId(long parentId, User user) {
        List<Folder> folderList = new ArrayList<>();
        Folder parentFolder;
        if (parentId != -1) {
            folderList = folderDao.getFoldersByParentId(parentId);
            parentFolder = folderDao.getFolderById(parentId);
        } else {
            parentFolder = folderDao.getParentFolderByUser(user);
        }
        FolderResponse parentFolderResponse = new FolderResponse()
                .setId(parentFolder.getId())
                .setVersion(parentFolder.getVersion())
                .setName(parentFolder.getName())
                .setParentFolderId(parentFolder.getParentFolderId());
        return connectFolderToChildrenFolder(parentFolderResponse, folderList);
    }

    @Transactional
    public FolderResponse createFolder(User user, FolderRequest request) throws NotFoundException, UnauthorizedException {
        if (request.getParentFolderId() == null) {
            logger.error("Cannot create a root folder.");
            throw new UnauthorizedException("folder.error.root");
        }
        if (request.getName().length() > 255) {
            logger.error("The folder name cannot exceed 255 characters.");
            throw new IllegalArgumentException("Le nom du dossier ne peut pas dépasser 255 caractères.");
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
        if (request.getFolderToBeMoved() == null || request.getMoveTo() == null) {
            logger.error("Folder to be moved can not be null");
            throw new UnauthorizedException("Impossible de déplacer le dossier.");
        } else {
            Folder toBeMoved = folderDao.getFolderById(request.getFolderToBeMoved());
            Folder moveToFolder = folderDao.getFolderById(request.getMoveTo());
            if (toBeMoved == null || moveToFolder == null) {
                logger.error("Folder not found.");
                throw new NotFoundException("Dossier introuvable.");
            } else if (folderDao.getParentFolderByUser(user).getId() == request.getFolderToBeMoved()) {
                logger.error("Root folder can not be moved");
                throw new UnauthorizedException("Dossier racine ne peut pas être déplacer.");
            } else if (moveToFolder.getParentFolderId() != null && moveToFolder.getParentFolderId().equals(request.getFolderToBeMoved())) {
                logger.error("Folder parent can not be moved to it's child folder");
                throw new UnauthorizedException("Dossier parent ne peut pas être déplacé vers son dossier enfant.");
            } else {
                return moveFolder(toBeMoved, request.getMoveTo());
            }
        }

    }

    MessageResponse moveFolder(Folder folderToBeMoved, Long toMoveToFolder) {
        List<Folder> folders = folderDao.getByParentFolderId(toMoveToFolder);
        for (Folder folder : folders) {
            if (folder.getName().equals(folderToBeMoved.getName())) {
                List<Photo> photosToBeMoved = photoDao.getPhotosByFolder(folderToBeMoved);
                List<Folder> foldersToBeMoved = folderDao.getByParentFolderId(folderToBeMoved.getId());
                for (Photo p : photosToBeMoved) {
                    p.setFolder(folder);
                }
                for (Folder f : foldersToBeMoved) {
                    f.setParentFolderId(folder.getId());
                }
                return new MessageResponse("success: Le déplacement du dossier est terminé.");
            }
        }
        folderToBeMoved.setParentFolderId(toMoveToFolder);
        return new MessageResponse("success: Le déplacement du dossier est terminé.");
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
}
