package com.pulserise.pulserisebackend.Service;

import com.pulserise.pulserisebackend.Model.User;
import com.pulserise.pulserisebackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Since we're using email as username for authentication, try to find by email first
        User user = userRepository.findByEmail(username)
                .orElseGet(() -> {
                    // Fallback to username lookup for backward compatibility
                    return userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email/username: " + username));
                });

        return UserDetailsImpl.build(user);
    }
}