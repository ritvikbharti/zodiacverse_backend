package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.ChangePasswordRequest;
import com.ritvik.zodiacverseBackend.dto.UpdateProfileRequest;
import com.ritvik.zodiacverseBackend.dto.UserResponse;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import com.ritvik.zodiacverseBackend.service.UserService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepository;
    private final UserService userService;

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

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName();
        User updatedUser = userService.updateProfile(email, request);

        UserResponse response = UserResponse.builder()
                .id(updatedUser.getId())
                .fullName(updatedUser.getFullname())
                .email(updatedUser.getEmail())
                .phone(updatedUser.getPhone())
                .role(updatedUser.getRole().name())

                .build();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .message("Profile updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String email = authentication.getName();
        userService.changePassword(email, request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Password changed successfully")
                        .data("DONE")
                        .build()
        );
    }
}