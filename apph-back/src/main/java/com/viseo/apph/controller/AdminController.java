package com.viseo.apph.controller;

import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUserList() {
        return ResponseEntity.ok(userService.getUserList());
    }
}
