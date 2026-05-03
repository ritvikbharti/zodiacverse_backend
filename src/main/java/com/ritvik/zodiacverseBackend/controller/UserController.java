package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.UserResponse;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepo userRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .message("User fetched successfully")
                        .data(response)
                        .build()
        );
    }
}