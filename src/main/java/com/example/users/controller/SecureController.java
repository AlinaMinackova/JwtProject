package com.example.users.controller;

import com.example.users.security.JwtProvider;
import com.example.users.security.RefreshTokenService;
import com.example.users.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SecureController {

    private final AuthService authService;

    @PreAuthorize("hasRole('PREMIUM_USER')")
    @GetMapping("hello/user")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("Hello PREMIUM_USER!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("hello/admin")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello ADMIN!");
    }

    @PreAuthorize("hasRole('GUEST')")
    @GetMapping("hello/guest")
    public ResponseEntity<String> helloGuest() {
        return ResponseEntity.ok("Hello GUEST!");
    }

}