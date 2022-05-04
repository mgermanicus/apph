package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.dto.*;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.persistence.NoResultException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<IResponseDTO> getUserPhotos(@RequestHeader("Authorization") String token, @RequestParam int pageSize, @RequestParam int page) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody();
            PaginationResponse response = photoService.getUserPhotos(claims.get("login").toString(), pageSize, page);
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
            Claims claims = Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody();
            MultipartFile file = photoRequest.getFile();
            String format = photoService.getFormat(file);
            Set<Tag> tags = photoRequest.getTags();
            String name = photoRequest.getName();
            Photo photo = photoService.addPhoto(claims, name);
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
            Photo photo = photoService.getPhotoById(photoRequest.getId(), userId);
            PhotoResponse photoResponse = photoService.download(photoRequest.getId() + photo.getFormat()).setTitle(photo.getTitle()).setFormat(photo.getFormat());
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
