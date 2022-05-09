package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.InvalidFileException;
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

    @Transactional
    public Photo addPhoto(String title) {
        Photo photo = new Photo()
                .setTitle(title);
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
        List<Photo> usersPhoto = photoDao.getUserPhotos(idUser);
        List<PhotoResponse> usersPhotoResponse = new ArrayList<PhotoResponse>();
        for (Photo photo : usersPhoto) {
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

    public PhotoResponse download(long id) {
        byte[] photoByte = s3Dao.download(id + "");
        return new PhotoResponse().setData(photoByte);
    }

    public Photo getPhoto(long id, long idUser) throws FileNotFoundException {
        Photo photo = photoDao.getPhoto(id);
        if (photo == null)
            throw new FileNotFoundException();
        if ((idUser == photo.getIdUser()) {
            return photo;
        } else {
            throw new UnauthorizedException();
        }
    }
}
