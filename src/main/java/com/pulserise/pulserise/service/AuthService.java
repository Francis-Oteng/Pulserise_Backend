package com.pulserise.pulserise.service;

import com.pulserise.pulserise.entities.User;
import com.pulserise.pulserise.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.fetch.UserDTO;
import com.pulserise.pulserise.dto.fetch.UserLoginDTO;
import com.pulserise.pulserise.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public ApiResponse register(UserDTO user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ApiResponse(false, "Email already registered", null);
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmailVerified(false);
        newUser.setVerificationToken(UUID.randomUUID().toString());

        userRepository.save(newUser);

        emailService.sendVerificationEmail(user.getEmail(), newUser.getVerificationToken());

        return new ApiResponse(true, "User registered, verification email sent", null);
    }

    public ApiResponse login(UserLoginDTO userDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
            String token = jwtTokenProvider.generateToken(authentication);
            return new ApiResponse(true, "Login successful", token);
        } catch (AuthenticationException ex) {
            return new ApiResponse(false, "Invalid credentials", null);
        }
    }

    public String verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isEmpty())
            return "Invalid or expired verification token";

        User user = userOpt.get();
        user.setEmailVerified(true);
        user.setVerificationToken(null);

        userRepository.save(user);

        return "Email verified successfully";
    }

    public String requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty())
            return "If account exists, password reset link sent";

        User user = userOpt.get();
        user.setResetToken(UUID.randomUUID().toString());

        userRepository.save(user);
        emailService.sendResetEmail(email, user.getResetToken());

        return "Password reset link sent";
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);

        if (userOpt.isEmpty())
            return ("Invalid or expired token");

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);

        userRepository.save(user);

        return ("Password has been reset");
    }
}