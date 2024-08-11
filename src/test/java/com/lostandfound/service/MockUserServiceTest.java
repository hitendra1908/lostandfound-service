package com.lostandfound.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockUserServiceTest {

    private MockUserService mockUserService;

    @BeforeEach
    void setUp() {
        mockUserService = new MockUserService();
    }

    @Test
    void shouldReturnUserName_whenUserIdExists() {
        Long userId = 1001L;

        String userName = mockUserService.getUserNameById(userId);

        assertEquals("John Doe", userName);
    }

    @Test
    void shouldReturnNull_whenUserIdDoesNotExists() {
        Long userId = 9999L;

        String userName = mockUserService.getUserNameById(userId);

        assertNull(userName);
    }

    @Test
    void shouldBeTrue_whenUserIdExists() {
        Long userId = 1002L;

        boolean exists = mockUserService.userExists(userId);

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalse_whenUserIdDoesNotExists() {
        Long userId = 9999L;

        boolean exists = mockUserService.userExists(userId);

        assertFalse(exists);
    }
}
