package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderRequest;
import com.viseo.apph.dto.FolderResponse;
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

    @Transactional
    public FolderResponse getFoldersByUser(String login) throws NotFoundException {
        User user = userDao.getUserByLogin(login);
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
    public FolderResponse createFolder(String login, FolderRequest request) throws NotFoundException, UnauthorizedException {
        if (request.getParentFolderId() == null) {
            logger.error("Cannot create a root folder.");
            throw new UnauthorizedException("Impossible de créer un dossier racine.");
        }
        Folder parentFolder = folderDao.getFolderById(request.getParentFolderId());
        User user = userDao.getUserByLogin(login);
        if (parentFolder == null) {
            logger.error("Parent folder not found.");
            throw new NotFoundException("Dossier parent introuvable.");
        }
        if (parentFolder.getUser().getId() != user.getId()) {
            logger.error("User doesn't have access to this folder.");
            throw new UnauthorizedException("L'utilisateur n'a pas accès à ce dossier.");
        }
        Folder folder = new Folder().setName(request.getName()).setParentFolderId(request.getParentFolderId()).setUser(user);
        folderDao.createFolder(folder);
        return getFoldersByUser(login);
    }

    Folder getParentFolder(List<Folder> folders) throws NotFoundException {
        for (Folder folder : folders) {
            if (folder.getParentFolderId() == null) {
                folders.remove(folder);
                return folder;
            }
        }
        logger.error("Parent folder not found.");
        throw new NotFoundException("Dossier parent introuvable.");
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
