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
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    static TokenManager tokenManager = new TokenManager() {};

    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<List<PhotoResponse>> getUserPhotos(@RequestHeader("Authorization") String token) {
        long userId = tokenManager.getIdOfToken(token);
        List<PhotoResponse> infoPhotos = photoService.getUserPhotos(userId);
        return ResponseEntity.ok(infoPhotos);
    }

    @PostMapping("/upload")
    public ResponseEntity<IResponseDTO> upload(@RequestHeader("Authorization") String token, @ModelAttribute PhotoRequest photoRequest) {
        try {
            long userId = tokenManager.getIdOfToken(token);
            String format = photoService.getFormat(photoRequest.getFile());
            Photo photo = photoService.addPhoto(photoRequest.getTitle(), format, userId);
            return ResponseEntity.ok(new MessageResponse(photoService.saveWithName(photoRequest.getFile(), photo.getId() + format)));
        } catch (IOException | S3Exception | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Le format du fichier n'est pas valide"));
        }
    }
}
