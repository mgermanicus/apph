package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.SettingService;
import com.viseo.apph.service.UserService;
import org.aspectj.weaver.ast.Not;
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
    SettingService settingService;
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

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/getSettings")
    public ResponseEntity<IResponseDto> getSettings() {
        return ResponseEntity.ok(settingService.getSettings());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/contact/add")
    public ResponseEntity<IResponseDto> addContact(@RequestBody UserRequest request) {
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(userService.addContact(user, request));
        } catch (NoResultException nre) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("user.error.notExist"));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/contact/get")
    public ResponseEntity<IResponseDto> getContacts(){
        try {
            User user = utils.getUser();
            return ResponseEntity.ok(userService.getContacts(user));
        } catch (NoResultException nre) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("user.error.notExist"));
        }
    }
}
