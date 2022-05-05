package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    static TokenManager tokenManager = new TokenManager() {};

    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<IResponseDTO> getUserPhotos(@RequestHeader("Authorization") String token, @RequestBody PaginationRequest request) {
        try {
            String userLogin = tokenManager.getLoginOfToken(token);
            PaginationResponse response = photoService.getUserPhotos(userLogin, request);
            return ResponseEntity.ok(response);
        } catch (NoResultException nre) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("L'utilisateur n'existe pas."));
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Argument illégal."));
        }
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
