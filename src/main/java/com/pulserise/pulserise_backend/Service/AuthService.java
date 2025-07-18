package com.pulserise.pulserise_backend.Service;

import com.pulserise.pulserise_backend.Repository.UserRepository;
import com.pulserise.pulserise_backend.dto.AuthRequest;
import com.pulserise.pulserise_backend.dto.AuthResponse;
import com.pulserise.pulserise_backend.dto.RegisterRequest;
import com.pulserise.pulserise_backend.entities.User;
import com.pulserise.pulserise_backend.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return AuthResponse.builder()
                .message("Authentication successful")
                .success(true)
                .token(token)
                .userEmail(user.getEmail())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return AuthResponse.builder()
                .message("Registration successful")
                .success(true)
                .token(token)
                .userEmail(user.getEmail())
                .build();
    }

    public AuthResponse refreshToken(String token) {
        log.info("Refreshing token");
        String email = jwtTokenProvider.getUsernameFromToken(token);
        String newToken = jwtTokenProvider.generateToken(email);
        return AuthResponse.builder()
                .message("Token refreshed successfully")
                .success(true)
                .token(newToken)
                .userEmail(email)
                .build();
    }

    public void logout(String token) {
        log.info("Logging out user");
        // Implement token blacklist if needed
    }

    public boolean validateToken(String token) {
        log.info("Validating token");
        return jwtTokenProvider.validateToken(token);
    }
}
