package com.lostandfound.service;

import com.lostandfound.dto.UserRequestDto;
import com.lostandfound.exception.user.InvalidUserException;
import com.lostandfound.model.User;
import com.lostandfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(UserRequestDto userRequestDto) {
        validateUserRequest(userRequestDto);
        return userRepository.save(mapToUser(userRequestDto));
    }

    private void validateUserRequest(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            throw new InvalidUserException("Invalid User - User request can't be empty");
        }

        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();

        if (username == null || username.length() < 2) {
            throw new InvalidUserException("Invalid username - username should be minimum of 2 characters");
        }

        if (password == null || password.length() < 6) {
            throw new InvalidUserException("Invalid password - password should be a minimum of 6 characters");
        }
    }

    private User mapToUser(UserRequestDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(userDto.getRoles())
                .build();
    }
}
