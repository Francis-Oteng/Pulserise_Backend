package com.pulserise.pulserise_backend.Service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    // Example: Add user-related business logic here

    public String greetUser(String username) {
        return "Hello, " + username + "!";
    }

    // Add registration, authentication, profile, etc. methods as needed
}
