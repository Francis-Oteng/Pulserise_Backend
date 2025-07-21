package com.pulserise.pulserise.service;

import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.fetch.UserDTO;
import com.pulserise.pulserise.dto.fetch.UserLoginDTO;
import com.pulserise.pulserise.entities.User;
import com.pulserise.pulserise.repository.UserRepository;
import com.pulserise.pulserise.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public ApiResponse register(UserDTO registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return new ApiResponse(false, "Email Address already in use!", null);
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        userRepository.save(user);

        return new ApiResponse(true, "User registered successfully", null);
    }

    public ApiResponse login(UserLoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return new ApiResponse(true, "Login successful", jwt);
    }
}