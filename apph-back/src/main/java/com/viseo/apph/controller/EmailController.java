package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.SesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/email")
public class EmailController {
    @Autowired
    Utils utils;
    @Autowired
    SesService sesService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/send")
    public ResponseEntity<String> sendExample() {
        User user = utils.getUser();
        return ResponseEntity.ok(sesService.sendVerifyRegister(user));
    }
}
