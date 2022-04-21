package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;

    @Transactional
    public void addPhoto(String name) {
        Photo photo = new Photo().setName(name);
        photoDao.addPhoto(photo);
    }
}
