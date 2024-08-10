package com.lostandfound.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MockUserService {

    //TODO can also move this to separate service if there is time left
    private static final Map<Long, String> users = new HashMap<>();

    static {
        users.put(1001L, "John Doe");
        users.put(1002L, "Jane Smith");
    }

    public String getUserNameById(Long userId) {
        return users.get(userId);
    }

    public boolean userExists(Long userId) {
        return users.containsKey(userId);
    }
}
