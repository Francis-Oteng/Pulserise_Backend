package com.pulserise.pulserise_backend.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}

