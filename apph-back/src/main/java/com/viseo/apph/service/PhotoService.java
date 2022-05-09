package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Autowired
    UserDAO userDAO;

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
    public PaginationResponse getUserPhotos(String userLogin, int pageSize, int page) {
        User user = userDAO.getUserByLogin(userLogin);
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
        for (PhotoResponse photo :responseList) {
            response.addPhoto(photo);
        }
        return response;
    }

    public String saveWithName(MultipartFile file, String name) throws InvalidFileException, IOException {
        return s3Dao.upload(file, name);
    }
}
