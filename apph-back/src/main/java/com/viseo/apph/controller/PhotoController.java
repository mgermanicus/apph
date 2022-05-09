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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    TokenManager tokenManager = new TokenManager() {
    };
    @Autowired
    PhotoService photoService;

    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<List<PhotoResponse>> getUserPhotos(@RequestHeader("token") String token) {
        int userId = tokenManager.getIdOfToken(token);
        List<PhotoResponse> infoPhotos = photoService.getUserPhotos(userId);
        return ResponseEntity.ok(infoPhotos);
    }

    @PostMapping("/upload")
    public ResponseEntity<IResponseDTO> upload(MultipartFile file, String name) {
        try {
            String format = photoService.getFormat(file);
            Photo photo = photoService.addPhoto(name);
            return ResponseEntity.ok(new MessageResponse(photoService.saveWithName(file, photo.getId() + format)));
        } catch (IOException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Le format du fichier n'est pas valide"));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<IResponseDTO> download(@RequestHeader("Authorization") String token, @RequestBody PhotoRequest photoRequest) {
        try {
            int userId = tokenManager.getIdOfToken(token);
            Photo photo = photoService.getPhoto(photoRequest.getId(), userId);
            PhotoResponse photoResponse = photoService.download(photoRequest.getId()).setTitle(photo.getTitle()).setFormat(photo.getFormat());
            return ResponseEntity.ok(photoResponse);
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors du téléchargement"));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Le fichier n'existe pas"));
        }
    }
}
