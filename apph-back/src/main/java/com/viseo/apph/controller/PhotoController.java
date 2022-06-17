package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.*;
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
import java.util.List;

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
    public ResponseEntity<IResponseDto> getUserPhotos(@RequestBody FilterRequest filterRequest) {
        try {
            User user = utils.getUser();
            PaginationResponse response;
            response = photoService.getUserPhotos(user, filterRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("photoTable.error.illegalArgument"));
        } catch (InvalidObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("photoTable.error.invalidObject"));
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
    @PostMapping("/editInfos")
    public ResponseEntity<IResponseDto> editInfos(@ModelAttribute PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(new MessageResponse(photoService.editPhotoInfos(user, photoRequest)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<IResponseDto> upload(@ModelAttribute PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(new MessageResponse(photoService.addPhoto(user, photoRequest)));
        } catch (IOException | S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("upload.error.upload"));
        } catch (InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("upload.error.wrongFormat"));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (NoResultException | NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("upload.error.folderNotExist"));
        } catch (ConflictException ce) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(ce.getMessage()));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(iae.getMessage()
            ));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("download.error.download"));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("download.error.fileNotExist"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<IResponseDto> delete(@RequestBody PhotosRequest photosRequest) {
        try {
            User user = utils.getUser();
            photoService.deletePhotos(user, photosRequest.getIds());
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("photo.successDelete"));
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("photo.failDelete"));
        }

    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "folder/move")
    public ResponseEntity<IResponseDto> movePhotosToFolder(@RequestBody PhotosRequest photosRequest) {
        try {
            User user = utils.getUser();
            MessageListResponse response = photoService.movePhotosToFolder(user, photosRequest);
            return ResponseEntity.ok(response);
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "download/zip")
    public ResponseEntity<IResponseDto> downloadZip(@RequestBody PhotosRequest photosRequest) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(photoService.downloadZip(user, photosRequest));
        } catch (S3Exception | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("download.error.download"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (MaxSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new MessageResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/reupload")
    public ResponseEntity<IResponseDto> changePhotoFile(@ModelAttribute PhotoRequest photoRequest) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(new MessageResponse(photoService.changePhotoFile(user.getId(), photoRequest)));
        } catch (FileNotFoundException fnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(fnfe.getMessage()));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (InvalidFileException ife) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("upload.error.wrongFormat"));
        } catch (S3Exception | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("upload.error.reUpload"));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/update", produces = "application/json")
    public ResponseEntity<IResponseDto> updatePhotoInfo(@RequestBody PhotoRequest photoRequest) {
        User user = utils.getUser();
        try {
            return ResponseEntity.ok(photoService.updatePhotoFolder(user, photoRequest));
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/{photoIds}")
    public ResponseEntity<IResponseDto> getPhotosByIds(@PathVariable List<Long> photoIds) {
        try {
            User user = utils.getUser();
            PhotoListResponse responseList = photoService.getPhotosByIds(photoIds, user);
            return ResponseEntity.ok().body(responseList);
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        }
    }
}
