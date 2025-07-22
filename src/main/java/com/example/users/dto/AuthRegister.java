package com.example.users.dto;

import jakarta.persistence.Entity;
import lombok.Data;

import java.util.Set;


@Data
public class AuthRegister {
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}