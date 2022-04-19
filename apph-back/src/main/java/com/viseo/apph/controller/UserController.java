package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import javax.persistence.NoResultException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/auth")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/signIn")
    public ResponseEntity login(@RequestBody UserRequest userRequest)
    {
        try
        {
            User user = userService.login(userRequest.login,userRequest.password);
            Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

            String jws = Jwts.builder().claim("login",user.getLogin()).signWith(key).compact();
            return ResponseEntity.ok(jws);
        }
        catch(IllegalArgumentException | NoResultException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Email or Password.");
        }

    }



}
