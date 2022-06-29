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
        User user = utils.getUser();
        return ResponseEntity.ok(new UserResponse().setLogin(user.getLogin()).setFirstname(user.getFirstname())
                .setLastname(user.getLastname()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> editUserInfo(@RequestHeader("Authorization") String token, @RequestBody UserRequest request) {
        try {
            User user = utils.getUser();
            String newToken = userService.editUser(user, request, token);
            return ResponseEntity.ok(newToken);
        } catch (NullPointerException | NotFoundException | NoResultException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user.error.sessionBindUserNotExist");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        }
    }
}
