package com.fintrack.auth.service;

import com.fintrack.auth.dto.LoginRequest;
import com.fintrack.auth.dto.RegisterRequest;
import com.fintrack.auth.dto.response.LoginResponse;
import com.fintrack.auth.dto.response.RegisterResponse;
import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.EmailAlreadyExistsException;
import com.fintrack.security.jwt.JwtService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new RegisterResponse(
                "User Registered Successfully!");
    }

    public LoginResponse login(LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new LoginResponse("User not found");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new LoginResponse("Invalid Password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                "Login Successful",
                token);
    }
}