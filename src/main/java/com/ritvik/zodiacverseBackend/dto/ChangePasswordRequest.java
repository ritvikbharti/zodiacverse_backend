package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Old password required")
    private String oldPassword;

    @NotBlank(message = "New password required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}