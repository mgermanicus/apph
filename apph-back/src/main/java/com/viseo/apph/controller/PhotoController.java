package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<IResponseDTO> upload(MultipartFile file, String name) {
        try {
            return ResponseEntity.ok(new MessageResponse(photoService.upload(file, name)));
        } catch (IOException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Le format du fichier n'est pas valide"));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<IResponseDTO> download(@RequestBody PhotoRequest photoRequest) {
        Photo photo = photoService.getPhoto(photoRequest.getId());
        PhotoResponse photoResponse = photoService.download(photoRequest.getId()).setName(photo.getName()).setExtension(photo.getExtension());
        return ResponseEntity.ok(photoResponse);
    }
}
