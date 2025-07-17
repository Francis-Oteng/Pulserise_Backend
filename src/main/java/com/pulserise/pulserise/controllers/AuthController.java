package com.pulserise.pulserise.controllers;

import com.pulserise.pulserise.dto.User;
import com.pulserise.pulserise.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse> register(@RequestBody User userDto) {
        ApiResponse response = authService.register(userDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody User userDto) {
        // This will return a response containing the JWT token if credentials are valid
        ApiResponse response = authService.login(userDto);
        return ResponseEntity.ok(response);
    }
} 