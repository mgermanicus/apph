package com.viseo.apph.controller;

import com.viseo.apph.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @PostMapping("/upload")
    public void upload(MultipartFile filePhoto) throws IOException {
        photoService.addPhoto(filePhoto);
    }
}
