package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.User;
import com.viseo.apph.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "${front-server}")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{login}")
    public ResponseEntity getUserInfo(@RequestHeader("Authentication") String token, @PathVariable("login") String login) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody();
            User user = userService.getUser(login, claims);
            if (user != null)
                return ResponseEntity.ok(new User().setLogin(user.getLogin()).setFirstname(user.getFirstname())
                        .setLastname(user.getLastname()));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Authorized");
        } catch (NoResultException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not valid");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        }
    }
}
