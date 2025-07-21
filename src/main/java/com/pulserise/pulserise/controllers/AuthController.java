package com.pulserise.pulserise.controllers;

import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.fetch.UserDTO;
import com.pulserise.pulserise.dto.fetch.UserLoginDTO;
import com.pulserise.pulserise.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserDTO user) {
        ApiResponse response = authService.register(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginDTO userDto) {
        ApiResponse response = authService.login(userDto);
        return ResponseEntity.ok(response);
    }
}