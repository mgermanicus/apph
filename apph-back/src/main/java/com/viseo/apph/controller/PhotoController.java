package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @Autowired
    S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<IResponseDTO> upload(MultipartFile file, String name) {
        try {
            String format = photoService.getFormat(file);
            Photo photo = photoService.addPhoto(name);
            return ResponseEntity.ok(new MessageResponse(s3Service.saveWithName(file, photo.getId() + format)));
        } catch (IOException | InvalidFileException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }
}
