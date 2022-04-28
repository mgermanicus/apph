package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.service.FolderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;

@CrossOrigin
@RestController
@RequestMapping("/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @ResponseBody
    @GetMapping("/")
    public ResponseEntity<IResponseDTO> getFoldersByUser(@RequestHeader("Authentication") String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody();
            FolderResponse folder = folderService.getFoldersByUser(claims.get("login").toString());
            return ResponseEntity.ok(folder);
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(nfe.getMessage()));
        } catch (NoResultException nre) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("L'utilisateur n'existe pas."));
        }
    }
}
