package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Autowired
    S3Dao s3Dao;

    @Autowired
    UserService userService;

    @Transactional
    public Photo addPhoto(String title, String format) {
        Photo photo = new Photo()
                .setTitle(title)
                .setFormat(format);
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
        User user = userService.getUserById(idUser);
        List<Photo> usersPhoto = photoDao.getUserPhotos(user);
        List<PhotoResponse> usersPhotoResponse = new ArrayList<>();
        for(Photo photo:usersPhoto) {
            PhotoResponse photoResponse = new PhotoResponse()
                    .setTitle(photo.getTitle())
                    .setCreationDate(photo.getCreationDate())
                    .setSize(photo.getSize())
                    .setTags(photo.getTags())
                    .setDescription(photo.getDescription())
                    .setShootingDate(photo.getShootingDate())
                    .setUrl("fake url"); //TODO RECUPERER LE VRAI URL
            usersPhotoResponse.add(photoResponse);
        }
        return usersPhotoResponse;
    }

    public String saveWithName(MultipartFile file, String name) throws InvalidFileException, IOException {
        return s3Dao.upload(file, name);
    }
}
