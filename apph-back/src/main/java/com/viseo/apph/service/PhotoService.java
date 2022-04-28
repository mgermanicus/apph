package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Transactional
    public Photo addPhoto(String name) {
        Photo photo = new Photo().setName(name);
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
}
