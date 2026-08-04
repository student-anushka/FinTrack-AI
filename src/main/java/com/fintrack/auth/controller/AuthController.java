package com.fintrack.auth.controller;

import com.fintrack.auth.dto.RegisterRequest;
import com.fintrack.auth.dto.response.RegisterResponse;
import com.fintrack.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.fintrack.auth.dto.LoginRequest;
import com.fintrack.auth.dto.response.LoginResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {

        return authService.register(request);

    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        return authService.login(request);

    }
}