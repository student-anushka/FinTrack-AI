package com.fintrack.auth.controller;

import com.fintrack.auth.dto.RegisterRequest;
import com.fintrack.auth.dto.response.RegisterResponse;
import com.fintrack.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.fintrack.auth.dto.LoginRequest;
import com.fintrack.auth.dto.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
public ResponseEntity<RegisterResponse> register(
        @Valid @RequestBody RegisterRequest request) {

    RegisterResponse response = authService.register(request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    @PostMapping("/login")
public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request) {

    LoginResponse response = authService.login(request);

    return ResponseEntity.ok(response);
}
}