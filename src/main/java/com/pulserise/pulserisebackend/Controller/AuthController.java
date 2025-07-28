package com.pulserise.pulserisebackend.Controller;
import com.pulserise.pulserisebackend.Dto.JwtResponse;
import com.pulserise.pulserisebackend.Dto.LoginRequest;
import com.pulserise.pulserisebackend.Dto.SignupRequest;
import com.pulserise.pulserisebackend.Service.AuthService;
import com.pulserise.pulserisebackend.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User already exists");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", "An error occurred during registration: " + e.getMessage());
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testPublicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public endpoint is working!");
        response.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> testProtectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Protected endpoint is working!");
        response.put("user", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(response);
    }
}