package com.pulserise.pulserise_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String message;
    private boolean success;
    private String token;
    private String refreshToken;
    private String userEmail;
    private String userRole;
    private Long expiresIn; // Token expiration time in milliseconds
}