package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.User;
import com.viseo.apph.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.security.Key;

@RestController
@RequestMapping("/user")
// @CrossOrigin(origins = "${front-server}")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{login}")
    public ResponseEntity login(@RequestHeader("Authentication") String token, @PathVariable("login") String login) {
        System.out.println(token);
        Key key = JwtConfig.getKey();
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        System.out.println(claims.getExpiration());
        try {
            User user = userService.getUser(login);
            return ResponseEntity.ok(new User().setLogin(user.getLogin()).setFirstname(user.getFirstname())
                    .setLastname(user.getLastname()));
        } catch (NoResultException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }
    }
}
