package com.example.users.service;

import com.example.users.dto.AuthRegister;
import com.example.users.dto.AuthRequest;
import com.example.users.dto.AuthResponse;
import com.example.users.models.RefreshToken;
import com.example.users.models.Role;
import com.example.users.models.User;
import com.example.users.repository.RoleRepository;
import com.example.users.repository.UserRepository;
import com.example.users.security.JwtProvider;
import com.example.users.security.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(RoleRepository roleRepository, UserRepository userRepo, PasswordEncoder encoder, JwtProvider jwtProvider, RefreshTokenService refreshTokenService) {
        this.roleRepository = roleRepository;
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void register(AuthRegister request) {
        if (userRepo.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already used");
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {        // Назначить роль GUEST по умолчанию
            Role guestRole = roleRepository.findByName(Role.RoleName.GUEST).orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(guestRole);
        } else {
            for (String roleStr : request.getRoles()) {
                Role.RoleName roleName;
                try {
                    roleName = Role.RoleName.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + roleStr);
                }
                Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);
        userRepo.save(user);
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (!encoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Wrong password");
        System.out.println();

        String token = jwtProvider.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken());
    }
}