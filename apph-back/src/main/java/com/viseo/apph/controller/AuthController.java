package com.viseo.apph.controller;


import com.viseo.apph.dto.LoginRequest;
import com.viseo.apph.dto.UserRequest;
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
            return ResponseEntity.ok().body("Utilisateur crée");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email déjà utilisé.");
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        }
    }
}
