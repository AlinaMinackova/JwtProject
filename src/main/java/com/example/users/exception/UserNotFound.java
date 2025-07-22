package com.example.users.exception;

public class UserNotFound extends RuntimeException{

    public UserNotFound(final String message) {
        super(message);
    }
}
