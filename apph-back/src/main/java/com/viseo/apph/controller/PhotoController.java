package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.ResponseDTO;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @Autowired
    S3Service s3Service;

    @GetMapping(value="/1", produces = "application/json")
   public ResponseEntity getInfoPhoto() {
        List<Photo> infoPhotos = photoService.getInfoPhoto(1);

        return ResponseEntity.ok(infoPhotos);
   }
}
