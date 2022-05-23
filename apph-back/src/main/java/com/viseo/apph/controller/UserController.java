package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    Utils utils;

    @GetMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<IResponseDto> getUserInfo() {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(new UserResponse().setLogin(user.getLogin()).setFirstname(user.getFirstname())
                    .setLastname(user.getLastname()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new MessageResponse("L'utilisateur n'est pas authentifié")));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> editUserInfo(@RequestHeader("Authorization") String token, @RequestBody UserRequest request) {
        try {
            User user = utils.getUser();
            String newToken = userService.editUser(user, request, token);
            return ResponseEntity.ok(newToken);
        } catch (NullPointerException | NotFoundException | NoResultException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'utilisateur lié à cette session n'existe pas");
        } catch (SignatureException | ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La session a expiré. Veuillez vous reconnecter");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ce login est déjà pris");
        }
    }
}
