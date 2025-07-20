package com.pulserise.pulserise_backend.Service;

import com.pulserise.pulserise_backend.entities.User;
import com.pulserise.pulserise_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.NonNull;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registration logic
    public User register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Set other fields as needed
        return userRepository.save(user);
    }

    // Authentication logic
    public boolean authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null); // This line is correct as is, assuming findByEmail returns Optional<User>
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return true;
        }
        return false;
    }
    // Additional methods for password reset, etc. can be added here

    // Request password reset (stub - implement token/email logic as needed)
    public boolean requestPasswordReset(String email) {
        return userRepository.findByEmail(email).isPresent();
        // In production, generate a token, save it, and send an email with the reset link.
    }

    // Perform password reset
    public boolean resetPassword(String email, String newPassword) {
        return userRepository.findByEmail(email).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
}

