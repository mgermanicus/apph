package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.ConflictException;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
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
import java.io.InvalidObjectException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;
    @Autowired
    Utils utils;
    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<IResponseDto> getUserFilteredPhotos(@RequestBody FilterRequest filterRequest) {

        try {
            User user = utils.getUser();
            PaginationResponse response;
            if (filterRequest.getFilters() == null) {
                response = photoService.getUserPhotos(user, filterRequest.getPageSize(), filterRequest.getPage());
            } else {
                System.out.println(filterRequest.getFilters()[0].getField());
                response = photoService.getUserFilteredPhotos(user, filterRequest.getPageSize(), filterRequest.getPage(), filterRequest);
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IndexOutOfBoundsException | InvalidObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Argument illégal."));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/folder/{folderId}")
    public ResponseEntity<IResponseDto> getPhotosByFolder(@PathVariable long folderId) {
        try {
            User user = utils.getUser();
            PhotoListResponse responseList = photoService.getPhotosByFolder(folderId, user);
            return ResponseEntity.ok().body(responseList);
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<IResponseDto> upload(@ModelAttribute PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(new MessageResponse(photoService.addPhoto(user, photoRequest)));
        } catch (IOException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Le format du fichier n'est pas valide"));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (NoResultException | NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Le dossier n'existe pas."));
        } catch (ConflictException ce) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(ce.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/download")
    public ResponseEntity<IResponseDto> download(@RequestBody PhotoRequest photoRequest) {
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

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<IResponseDto> delete(@RequestBody PhotosRequest photosRequest) {
        try {
            User user = utils.getUser();
            photoService.deletePhotos(user, photosRequest.getIds());
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Suppression effectuée avec succès"));
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de la suppression"));
        }

    }
}
