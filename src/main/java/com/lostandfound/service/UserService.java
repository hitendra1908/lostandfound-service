package com.lostandfound.service;

import com.lostandfound.dto.UserRequestDto;
import com.lostandfound.exception.user.InvalidRoleException;
import com.lostandfound.exception.user.InvalidUserException;
import com.lostandfound.model.Roles;
import com.lostandfound.model.User;
import com.lostandfound.repository.RolesRepository;
import com.lostandfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RolesRepository rolesRepository;

    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(UserRequestDto userRequestDto) {
        User validUser = returnvalidatUser(userRequestDto);
        return userRepository.save(validUser);
    }

    private User returnvalidatUser(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            throw new InvalidUserException("Invalid User - User request can't be empty");
        }

        String name = userRequestDto.getName();
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();

        if (name == null || name.length() < 2) {
            throw new InvalidUserException("Invalid name - name should be minimum of 2 characters");
        }

        if (username == null || username.length() < 2) {
            throw new InvalidUserException("Invalid username - username should be minimum of 2 characters");
        }

        if (password == null || password.length() < 6) {
            throw new InvalidUserException("Invalid password - password should be a minimum of 6 characters");
        }

        Set<Roles> validRoles = userRequestDto.getRoles().stream()
                .map(this :: checkRoles)
                .collect(Collectors.toSet());

        return mapToUser(userRequestDto, validRoles);
    }

    private Roles checkRoles(String role) {
        return rolesRepository.findByName(role).orElseThrow(
                () -> new InvalidRoleException("Invalid Role: application doesn't have any role: "+role));
    }

    private User mapToUser(UserRequestDto userDto, Set<Roles> validRole) {
        return User.builder()
                .name(userDto.getName())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(validRole)
                .build();
    }
}
