package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderRequest;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.exception.MaxSizeExceededException;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/folder")
public class FolderController {
    @Autowired
    FolderService folderService;
    @Autowired
    Utils utils;

    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/{folderId}")
    public ResponseEntity<IResponseDto> getFoldersByUser(@PathVariable long folderId) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(folderService.getFoldersByParentId(folderId, user));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(iae.getMessage()));
        }
    }

    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<IResponseDto> createFolder(@RequestBody FolderRequest request) {
        try {
            User user = utils.getUser();
            FolderResponse folder = folderService.createFolder(user, request);
            return ResponseEntity.ok(folder);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("folder.error.existingFolder"));
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(iae.getMessage()));
        }
    }

    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/move")
    public ResponseEntity<IResponseDto> moveFolder(@RequestBody FolderRequest request) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(folderService.moveFolder(user, request));
        } catch (UnauthorizedException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "download")
    public ResponseEntity<IResponseDto> downloadFolderToZip(@RequestBody FolderRequest request) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(folderService.downloadFolder(user, request));
        } catch (S3Exception | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("download.error.download"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("request.error.unauthorizedResource"));
        } catch (MaxSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new MessageResponse(e.getMessage()));
        }
    }
}
