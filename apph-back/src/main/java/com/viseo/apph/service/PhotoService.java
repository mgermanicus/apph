package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Transactional
    public Photo addPhoto(String title, long idUser, int size, String description, String tags, Date creationDate, Date shootingDate) {
        Photo photo = new Photo()
                .setTitle(title)
                .setIdUser(idUser)
                .setSize(size)
                .setDescription(description)
                .setTags(tags)
                .setCreationDate(creationDate)
                .setShootingDate(shootingDate);
        return photoDao.addPhoto(photo);
    }

    public String getFormat(MultipartFile file) throws InvalidFileException {
        String contentType = file.getContentType();
        if(contentType != null && contentType.startsWith("image/")){
            String[] types = contentType.split("/");
            return "."+types[1] ;
        } else {
            throw new InvalidFileException("Wrong file format");
        }
    }

    @Transactional
    public List<Photo> getInfoPhoto(long idUser){
        return photoDao.getUserByLogin(idUser);
    }
}
