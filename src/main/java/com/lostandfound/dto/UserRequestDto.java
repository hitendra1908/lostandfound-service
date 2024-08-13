package com.lostandfound.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {

    private String name;

    private String username;

    private String password;

    private List<String> roles;
}
