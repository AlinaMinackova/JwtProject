package com.example.users.controller;

import com.example.users.dto.AuthRegister;
import com.example.users.dto.AuthRequest;
import com.example.users.dto.AuthResponse;
import com.example.users.models.RefreshToken;
import com.example.users.models.User;
import com.example.users.security.JwtProvider;
import com.example.users.security.RefreshTokenService;
import com.example.users.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//{
//    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyX2FkbWluIiwicm9sZXMiOlsiQURNSU4iXSwiaWF0IjoxNzUzMTYxMTc3LCJleHAiOjE3NTMxNjIwNzd9.nbsf3gXdzG8qhn9GU99yYQDoWUqqTYGglUbk3E7eqzF6ea9CPwa3bDFKm-S1rmhw9lqVl0vzFeHLXNEI6rHTQA",
//    "refreshToken": "56b30a79-bb85-43d9-b3c4-36fef7a2d176"
//}

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtProvider jwtProvider) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/all")
    public List<User> all() {
        return authService.getAllUsers();
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRegister request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshTokenService.isExpired(token)) {
            refreshTokenService.deleteByUserId(token.getUser().getId());
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken = jwtProvider.generateToken(token.getUser());
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revoke(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        refreshTokenService.deleteByUserId(token.getUser().getId());
        return ResponseEntity.ok("Token revoked");
    }
}