package com.viseo.apph.controller;

import com.viseo.apph.dto.*;
import com.viseo.apph.exception.ExpiredLinkException;
import com.viseo.apph.exception.InvalidTokenException;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping(value = "/signIn")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        if (!userService.verifyUserVerified(loginRequest))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("signup.error.emailNotVerified");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> register(@RequestBody UserRequest userRequest) {
        try {
            userService.registerUser(userRequest);
            return ResponseEntity.ok().body("signup.created");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("signup.error.emailUsed");
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            userService.forgotPassword(forgotPasswordRequest.getLogin(), forgotPasswordRequest.getLanguage());
            return ResponseEntity.ok().body("");
        } catch (NoResultException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user.error.emailNotFound");
        }
    }

    @PostMapping("/checkToken")
    public ResponseEntity<String> checkToken(@RequestBody TokenRequest tokenRequest) {
        try {
            userService.checkToken(tokenRequest.getToken());
            return ResponseEntity.ok().body("");
        } catch (InvalidTokenException | ExpiredLinkException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getPassword());
            return ResponseEntity.ok().body("");
        } catch (NoResultException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user.error.emailNotFound");
        }
    }

    @PostMapping("/activateUser")
    public ResponseEntity<String> activateUser(@RequestBody TokenRequest tokenRequest) {
        try {
            return ResponseEntity.ok(userService.activeUser(tokenRequest.getToken()));
        } catch (InvalidTokenException | ExpiredLinkException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
