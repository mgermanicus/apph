package com.viseo.apph.controller;

import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.SettingRequest;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.service.SettingService;
import com.viseo.apph.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    SettingService settingService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUserList() {
        return ResponseEntity.ok(userService.getUserList());
    }

    @GetMapping("/getSettings")
    public ResponseEntity<IResponseDto> getSettings() {
        return ResponseEntity.ok(settingService.getSettings());
    }

    @PostMapping("/updateSettings")
    public ResponseEntity<IResponseDto> updateSettings(@RequestBody SettingRequest settingRequest) {
        return ResponseEntity.ok(settingService.updateSettings(settingRequest));
    }
}
