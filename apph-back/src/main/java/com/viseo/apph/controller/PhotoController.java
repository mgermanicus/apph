package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @Autowired
    S3Service s3Service;

    @PostMapping("/upload")
    public String upload(MultipartFile file, String name) {
        Photo photo = photoService.addPhoto(name);
        return s3Service.saveWithName(file,photo.getId()+"");
    }
}
