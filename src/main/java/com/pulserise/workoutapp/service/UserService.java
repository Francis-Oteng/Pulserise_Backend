package com.pulserise.workoutapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulserise.workoutapp.security.JwtAuthEntryPoint;
import com.pulserise.workoutapp.security.JwtAuthTokenFilter;
import com.pulserise.workoutapp.security.UserDetailsServiceImpl;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}