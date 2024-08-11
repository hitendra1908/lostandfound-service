package com.lostandfound.dto;

import com.lostandfound.model.Roles;
import lombok.Data;

import java.util.Set;

@Data
public class UserRequestDto {

    private String name;

    private String username;

    private String password;

    private Set<Roles> roles;
}
