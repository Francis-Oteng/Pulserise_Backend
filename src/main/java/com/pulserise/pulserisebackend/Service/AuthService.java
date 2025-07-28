package com.pulserise.pulserisebackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pulserise.pulserisebackend.Dto.JwtResponse;
import com.pulserise.pulserisebackend.Dto.LoginRequest;
import com.pulserise.pulserisebackend.Dto.SignupRequest;
import com.pulserise.pulserisebackend.exception.UserAlreadyExistsException;
import com.pulserise.pulserisebackend.Model.Role;
import com.pulserise.pulserisebackend.Model.User;
import com.pulserise.pulserisebackend.Repository.RoleRepository;
import com.pulserise.pulserisebackend.Repository.UserRepository;
import com.pulserise.pulserisebackend.Security.JwtUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.isProfileCompleted(),
                roles);
    }

    public void registerUser(SignupRequest signUpRequest) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        
        // Try to find ROLE_USER, create it if it doesn't exist
        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseGet(() -> {
                    System.out.println("ROLE_USER not found, creating it...");
                    Role newRole = new Role();
                    newRole.setName(Role.ERole.ROLE_USER);
                    return roleRepository.save(newRole);
                });
        
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        
        System.out.println("User registered successfully: " + signUpRequest.getUsername());
    }
}