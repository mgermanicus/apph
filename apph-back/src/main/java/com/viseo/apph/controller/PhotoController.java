package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileNotFoundException;
import javax.persistence.NoResultException;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    static TokenManager tokenManager = new TokenManager() {};

    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<IResponseDTO> getUserPhotos(@RequestHeader("Authorization") String token, @RequestParam int pageSize, @RequestParam int page) {
        try {
            String userLogin = tokenManager.getLoginOfToken(token);
            PaginationResponse response = photoService.getUserPhotos(userLogin, pageSize, page);
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
            Photo photoByRequest = photoService.getPhotoByRequest(photoRequest, userId);
            Photo photo = photoService.addPhoto(photoByRequest);
            return ResponseEntity.ok(new MessageResponse(photoService.saveWithName(photoRequest.getFile(), photo.getId() + photo.getFormat())));
        } catch (IOException | S3Exception | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Le format du fichier n'est pas valide"));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<IResponseDTO> download(@RequestHeader("Authorization") String token, @RequestBody PhotoRequest photoRequest) {
        try {
            int userId = tokenManager.getIdOfToken(token);
            Photo photo = photoService.getPhotoById(photoRequest.getId(), userId);
            PhotoResponse photoResponse = photoService.download(photoRequest.getId()).setTitle(photo.getTitle()).setFormat(photo.getFormat());
            return ResponseEntity.ok(photoResponse);
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors du téléchargement"));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Le fichier n'existe pas"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("L'utilisateur n'est pas autorisé à accéder à la ressource demandée"));
        }
    }
}
