package com.pulserise.pulserisebackend.Config;

import com.pulserise.pulserisebackend.Model.Role;
import com.pulserise.pulserisebackend.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.findByName(Role.ERole.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);
            System.out.println("Created ROLE_USER");
        }

        if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("Created ROLE_ADMIN");
        }

        System.out.println("Data initialization completed");
    }
}