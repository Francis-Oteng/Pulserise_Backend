package com.pulserise.pulserise_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.pulserise.pulserise_backend.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Object findByEmail(Long email);

    Object findByEmail(String email);

    }
