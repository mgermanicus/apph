package com.viseo.apph.service;

import com.google.gson.GsonBuilder;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FilterDto;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.dto.PhotoListResponse;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.ConflictException;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;
    @Autowired
    UserDao userDao;
    @Autowired
    TagService tagService;
    @Autowired
    FolderDao folderDao;
    @Autowired
    S3Dao s3Dao;

    @Transactional
    public String addPhoto(User user, PhotoRequest photoRequest) throws InvalidFileException, IOException, NotFoundException, UnauthorizedException, ConflictException {
        Folder folder;
        if (photoRequest.getFolderId() == -1) {
            folder = folderDao.getParentFolderByUser(user);
        } else {
            folder = folderDao.getFolderById(photoRequest.getFolderId());
        }
        if (folder == null)
            throw new NotFoundException("Le dossier n'existe pas.");
        if (folder.getUser().getId() != user.getId())
            throw new UnauthorizedException("L'utilisateur n'a pas accès à ce dossier.");
        if (photoDao.existNameInFolder(folder, photoRequest.getTitle()))
            throw new ConflictException("Titre déjà utilisé dans le dossier.");
        Set<Tag> allTags = tagService.createListTags(photoRequest.getTags(), user);
        Date shootingDate = photoRequest.getShootingDate() != null ? new GsonBuilder().setDateFormat("dd/MM/yyyy, hh:mm:ss").create().fromJson(photoRequest.getShootingDate(), Date.class) : new Date();
        Photo photo = new Photo()
                .setTitle(photoRequest.getTitle())
                .setFormat(getFormat(photoRequest.getFile()))
                .setUser(user)
                .setSize((photoRequest.getFile().getSize() + .0F) / 1000)
                .setDescription(photoRequest.getDescription())
                .setCreationDate(new Date())
                .setShootingDate(shootingDate)
                .setTags(allTags)
                .setFolder(folder);
        photo = photoDao.addPhoto(photo);
        return s3Dao.upload(photoRequest.getFile(), photo);
    }

    public String getFormat(MultipartFile file) throws InvalidFileException {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            String[] types = contentType.split("/");
            return "." + types[1];
        } else {
            throw new InvalidFileException("Wrong file format");
        }
    }

    @Transactional
    public PaginationResponse getUserPhotos(User user, int pageSize, int page) {
        List<Photo> userPhotos = photoDao.getUserPhotos(user);
        int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize;
        PaginationResponse response = new PaginationResponse().setTotalSize(userPhotos.size());
        List<PhotoResponse> responseList = userPhotos.subList(startIndex, Math.min(endIndex, userPhotos.size())).stream()
                .map(photo -> new PhotoResponse()
                        .setId(photo.getId())
                        .setTitle(photo.getTitle())
                        .setCreationDate(photo.getCreationDate())
                        .setSize(photo.getSize())
                        .setTags(photo.getTags())
                        .setDescription(photo.getDescription())
                        .setShootingDate(photo.getShootingDate())
                        .setUrl(s3Dao.getPhotoUrl(photo))
                        .setFormat(photo.getFormat())
                ).collect(Collectors.toList());
        for (PhotoResponse photo : responseList) {
            response.addPhoto(photo);
        }
        return response;
    }

    public PhotoResponse download(Long userId, PhotoRequest photoRequest) throws FileNotFoundException, UnauthorizedException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) {
            throw new FileNotFoundException();
        }
        if (userId != photo.getUser().getId()) {
            throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
        }
        byte[] photoByte = s3Dao.download(photo);
        return new PhotoResponse().setData(photoByte).setTitle(photo.getTitle()).setFormat(photo.getFormat());
    }

    @Transactional
    public void deletePhotos(User user, long[] ids) {
        for (long id : ids) {
            Photo photo = photoDao.getPhoto(id);
            if (photo != null && photo.getUser().getId() == user.getId()) {
                s3Dao.delete(photo);
                photoDao.deletePhoto(photo);
            }
        }
    }

    @Transactional
    public PhotoListResponse getPhotosByFolder(long folderId, User user) throws NotFoundException, UnauthorizedException {
        Folder folder = folderDao.getFolderById(folderId);
        if (folder == null)
            throw new NotFoundException("Le dossier n'existe pas.");
        if (folder.getUser().getId() != user.getId())
            throw new UnauthorizedException("L'utilisateur n'a pas accès à ce dossier.");
        List<Photo> photoList = photoDao.getPhotosByFolder(folder);
        PhotoListResponse response = new PhotoListResponse();
        photoList.forEach(photo -> response.addPhoto(new PhotoResponse()
                .setId(photo.getId())
                .setTitle(photo.getTitle())
                .setCreationDate(photo.getCreationDate())
                .setSize(photo.getSize())
                .setTags(photo.getTags())
                .setDescription(photo.getDescription())
                .setShootingDate(photo.getShootingDate())
                .setUrl(s3Dao.getPhotoUrl(photo))
                .setFormat(photo.getFormat())
        ));
        return response;
    }

    private String createFilterQuery(List<FilterDto> filters) throws InvalidObjectException {
        StringBuilder query = new StringBuilder("SELECT p FROM Photo p LEFT JOIN Tag t ON p.user = t.user WHERE p.user = :user");
        filters.sort(FilterDto::compareTo);
        String lastField = "first";
        for (FilterDto filter : filters) {
            if (!Objects.equals(filter.getField(), lastField)) {
                if (!lastField.equals("first")) {query.append(") AND");}
                if (lastField.equals("first")) {query.append(" AND");}
                query.append(" (");
                lastField = filter.getField();
            }
            query.append(filter.getFieldToSql()).append(" ");
            query.append(filter.getOperatorToSql()).append(" ");
            query.append(filter.getValueToSql()).append(" ");
        }
        if (!filters.isEmpty()) {query.append(")");}
        return query.toString();
    }
}
