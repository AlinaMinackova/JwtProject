package com.example.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice("com.example.users.controller")
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
    })
    public Map<String, String> handleException_BAD_REQUEST(final RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFound.class
    })
    public Map<String, String> handleException_NOT_FOUND(final RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }
}