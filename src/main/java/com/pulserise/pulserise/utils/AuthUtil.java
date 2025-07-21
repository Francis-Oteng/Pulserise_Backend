package com.pulserise.pulserise.utils;

import com.pulserise.pulserise.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    public Long getUserIdFromToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = bearerToken.substring(7);
        return jwtTokenProvider.getUserIdFromJWT(token);
    }
}
