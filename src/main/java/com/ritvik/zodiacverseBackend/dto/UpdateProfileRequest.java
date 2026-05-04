package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String profileImageUrl;
}