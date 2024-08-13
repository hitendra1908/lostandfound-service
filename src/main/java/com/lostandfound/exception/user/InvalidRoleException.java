package com.lostandfound.exception.user;

public class InvalidRoleException extends RuntimeException{
    public InvalidRoleException(String message) {
        super(message);
    }
}
