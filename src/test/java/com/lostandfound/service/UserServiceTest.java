package com.lostandfound.service;

import com.lostandfound.dto.UserRequestDto;
import com.lostandfound.exception.user.InvalidRoleException;
import com.lostandfound.exception.user.InvalidUserException;
import com.lostandfound.model.Roles;
import com.lostandfound.model.User;
import com.lostandfound.repository.RolesRepository;
import com.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDto validUserRequestDto;
    private UserRequestDto invalidUserRequestDto;

    @BeforeEach
    void setUp() {
        validUserRequestDto = new UserRequestDto();
        validUserRequestDto.setName("John Doe");
        validUserRequestDto.setUsername("johndoe");
        validUserRequestDto.setPassword("password123");
        validUserRequestDto.setRoles(List.of("ROLE_USER"));

        invalidUserRequestDto = new UserRequestDto();
        invalidUserRequestDto.setName("J");
        invalidUserRequestDto.setUsername("jd");
        invalidUserRequestDto.setPassword("pass");
        invalidUserRequestDto.setRoles(List.of("INVALID_ROLE"));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        var users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void saveUser_ShouldSaveValidUser() {
        Roles userRole = new Roles();
        userRole.setName("ROLE_USER");

        when(rolesRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(validUserRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        User savedUser = userService.saveUser(validUserRequestDto);

        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_ShouldThrowInvalidUserException_WhenUserRequestIsNull() {
        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> {
            userService.saveUser(null);
        });

        assertEquals("Invalid User - User request can't be empty", exception.getMessage());
    }

    @Test
    void saveUser_ShouldThrowInvalidUserException_WhenNameIsInvalid() {
        invalidUserRequestDto.setName("A");

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> {
            userService.saveUser(invalidUserRequestDto);
        });

        assertEquals("Invalid name - name should be minimum of 2 characters", exception.getMessage());
    }

    @Test
    void saveUser_ShouldThrowInvalidUserException_WhenUsernameIsInvalid() {
        invalidUserRequestDto.setUsername("A");

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> {
            userService.saveUser(invalidUserRequestDto);
        });

        assertEquals("Invalid name - name should be minimum of 2 characters", exception.getMessage());
    }

    @Test
    void saveUser_ShouldThrowInvalidUserException_WhenPasswordIsInvalid() {
        invalidUserRequestDto.setName("JOS");
        invalidUserRequestDto.setUsername("JOS1");
        invalidUserRequestDto.setPassword("123");

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> {
            userService.saveUser(invalidUserRequestDto);
        });

        assertEquals("Invalid password - password should be a minimum of 6 characters", exception.getMessage());
    }

    @Test
    void saveUser_ShouldThrowInvalidRoleException_WhenRoleIsInvalid() {
        invalidUserRequestDto.setName("test");
        invalidUserRequestDto.setUsername("username");
        invalidUserRequestDto.setPassword("password");

        when(rolesRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        InvalidRoleException exception = assertThrows(InvalidRoleException.class, () -> {
            userService.saveUser(invalidUserRequestDto);
        });

        assertEquals("Invalid Role: application doesn't have any role: INVALID_ROLE", exception.getMessage());
    }
}
