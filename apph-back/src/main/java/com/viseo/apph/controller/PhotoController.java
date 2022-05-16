package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.security.UserDetailsImpl;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.TagService;
import com.viseo.apph.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.persistence.NoResultException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    static Utils utils = new Utils() {};
    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<IResponseDTO> getUserPhotos(@RequestParam int pageSize, @RequestParam int page) {
        User user = utils.getUser();
        long userId = user.getId();
        try {
            PaginationResponse response = photoService.getUserPhotos(userId, pageSize, page);
            return ResponseEntity.ok(response);
        } catch (NoResultException nre) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("L'utilisateur n'existe pas."));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("User does not exist"));
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Argument illégal."));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<IResponseDto> upload(@ModelAttribute PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            long userId = user.getId();
            return ResponseEntity.ok(new MessageResponse(photoService.addPhoto(userId, photoRequest)));
        } catch (IOException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Le format du fichier n'est pas valide"));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/download")
    public ResponseEntity<IResponseDTO> download(@RequestHeader("Authorization") String token, @RequestBody PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            long userId = user.getId();
            return ResponseEntity.ok(photoService.download(userId, photoRequest));
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors du téléchargement"));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Le fichier n'existe pas"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("L'utilisateur n'est pas autorisé à accéder à la ressource demandée"));
        }
    }
}
