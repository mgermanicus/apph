package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Autowired
    S3Dao s3Dao;

    @Autowired
    UserDAO userDAO;

    @Transactional
    public Photo addPhoto(String title, String format, long userId) {
        User user = userDAO.getUserById(userId);
        Photo photo = new Photo()
                .setTitle(title)
                .setFormat(format)
                .setUser(user);
        return photoDao.addPhoto(photo);
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
    public List<PhotoResponse> getUserPhotos(long idUser) {
        User user = userDAO.getUserById(idUser);
        List<Photo> usersPhoto = photoDao.getUserPhotos(user);
        List<PhotoResponse> usersPhotoResponse = new ArrayList<>();
        for(Photo photo:usersPhoto) {
            PhotoResponse photoResponse = new PhotoResponse()
                    .setId(photo.getId())
                    .setTitle(photo.getTitle())
                    .setCreationDate(photo.getCreationDate())
                    .setSize(photo.getSize())
                    .setTags(photo.getTags())
                    .setDescription(photo.getDescription())
                    .setShootingDate(photo.getShootingDate())
                    .setUrl(s3Dao.getPhotoUrl(photo));
            usersPhotoResponse.add(photoResponse);
        }
        return usersPhotoResponse;
    }

    public String saveWithName(MultipartFile file, String name) throws InvalidFileException, IOException {
        return s3Dao.upload(file, name);
    }

    public PhotoResponse download(long id) {
        byte[] photoByte = s3Dao.download(id + "");
        return new PhotoResponse().setData(photoByte);
    }

    public Photo getPhoto(long id, long idUser) throws FileNotFoundException, UnauthorizedException {
        System.out.println("photo id : " + id);
        Photo photo = photoDao.getPhoto(id);
        if (photo == null)
            throw new FileNotFoundException();
        if (idUser == photo.getUser().getId()) {
            return photo;
        } else {
            throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
        }
    }
}
