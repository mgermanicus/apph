package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.exception.InvalidFileException;
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
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;
    @Autowired
    Utils utils ;
    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/infos", produces = "application/json")
    public ResponseEntity<IResponseDto> getUserPhotos(@RequestParam int pageSize, @RequestParam int page) {
        try {
            User user = utils.getUser();
            PaginationResponse response = photoService.getUserPhotos(user, pageSize, page);
            return ResponseEntity.ok(response);
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
            return ResponseEntity.ok(new MessageResponse(photoService.addPhoto(user, photoRequest)));
        } catch (IOException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de l'upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Le format du fichier n'est pas valide"));
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

    @DeleteMapping(value = "/delete")
    public ResponseEntity<IResponseDTO> delete(@RequestHeader("Authorization") String token, @RequestBody PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            photoService.deletePhotos(user.getId(), photoRequest.getIds());
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Suppression effectuée avec succès"));
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Une erreur est survenue lors de la suppression"));
        }

    }
}
