package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.AuthResponse;
import com.ritvik.zodiacverseBackend.dto.LoginRequest;
import com.ritvik.zodiacverseBackend.dto.RegisterRequest;
import com.ritvik.zodiacverseBackend.service.AuthService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .message("User registered successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }
}