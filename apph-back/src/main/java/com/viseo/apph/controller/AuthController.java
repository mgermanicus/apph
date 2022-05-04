package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.service.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.security.Key;
import java.util.Date;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/signIn")
    public ResponseEntity<String> login(@RequestBody UserRequest userRequest) {
        try {
            User user = userService.login(userRequest);
            Key key = JwtConfig.getKey();
            String jws = Jwts.builder().claim("login", user.getLogin()).claim("id", user.getId())
                    .claim("firstname", user.getFirstname()).claim("lastname", user.getLastname())
                    .setExpiration(new Date(System.currentTimeMillis() + 7_200_000)).signWith(key).compact();
            return ResponseEntity.ok(jws);
        } catch (IllegalArgumentException | NoResultException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe invalide.");
        }

    }

    @PostMapping("/signUp")
    public ResponseEntity register(@RequestBody UserRequest userRequest) {
        try {
            userService.registerUser(userRequest);
            return ResponseEntity.ok().body("Utilisateur crée");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email déjà utilisé.");
        }
    }
}
