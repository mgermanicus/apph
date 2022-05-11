package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Autowired
    UserDao userDao;

    @Autowired
    S3Dao s3Dao;

    @Transactional
    public Photo addPhoto(Photo photo) {
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
    public PaginationResponse getUserPhotos(String userLogin, int pageSize, int page) {
        User user = userDao.getUserByLogin(userLogin);
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
                ).collect(Collectors.toList());
        for (PhotoResponse photo : responseList) {
            response.addPhoto(photo);
        }
        return response;
    }

    public String saveWithName(MultipartFile file, String name) throws InvalidFileException, IOException {
        return s3Dao.upload(file, name);
    }

    public PhotoResponse download(String name) {
        byte[] photoByte = s3Dao.download(name);
        return new PhotoResponse().setData(photoByte);
    }

    public Photo getPhotoById(long id, long idUser) throws FileNotFoundException, UnauthorizedException {
        Photo photo = photoDao.getPhoto(id);
        if (photo == null)
            throw new FileNotFoundException();
        if (idUser == photo.getUser().getId()) {
            return photo;
        } else {
            throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
        }
    }

    public Photo getPhotoByRequest(PhotoRequest photoRequest, String login) throws InvalidFileException {
        User user = userDao.getUserByLogin(login);
        return new Photo()
                .setTitle(photoRequest.getTitle())
                .setFormat(getFormat(photoRequest.getFile()))
                .setUser(user)
                .setSize((photoRequest.getFile().getSize() + .0F) / 1000);
    }
}
