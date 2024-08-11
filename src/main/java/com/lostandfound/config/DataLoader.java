package com.lostandfound.config;

import com.lostandfound.model.Roles;
import com.lostandfound.model.User;
import com.lostandfound.repository.RolesRepository;
import com.lostandfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if roles already exist
        Roles adminRole = rolesRepository.findByName("ROLE_ADMIN").orElse(new Roles());
        adminRole.setName("ROLE_ADMIN");
        Roles savedAdminRole = rolesRepository.save(adminRole);

        Roles userRole = rolesRepository.findByName("ROLE_USER").orElse(new Roles());
        userRole.setName("ROLE_USER");
        Roles savedUserRole = rolesRepository.save(userRole);

        // Create admin user if it doesn't exist
        Optional<User> adminUserOptional = userRepository.findByUsername("admin");
        if (adminUserOptional.isEmpty()) {
            User adminUser = new User();
            adminUser.setName("David");
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            Set<Roles> adminRoles = new HashSet<>();
            adminRoles.add(savedAdminRole);
            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
        }

        // Create regular user if it doesn't exist
        Optional<User> regularUserOptional = userRepository.findByUsername("user");
        if (regularUserOptional.isEmpty()) {
            User regularUser = new User();
            regularUser.setName("John");
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            Set<Roles> userRoles = new HashSet<>();
            userRoles.add(savedUserRole);
            regularUser.setRoles(userRoles);
            userRepository.save(regularUser);
        }
    }
}
