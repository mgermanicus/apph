package com.viseo.apph.controller;

import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @ResponseBody
    @GetMapping("/{userId}")
    public ResponseEntity<IResponseDTO> getFoldersByUser(@PathVariable long userId) {
        try {
            FolderResponse folder = folderService.getFoldersByUser(userId);
            return ResponseEntity.ok(folder);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }
}
