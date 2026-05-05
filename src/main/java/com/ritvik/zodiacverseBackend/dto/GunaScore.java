package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GunaScore {
    private String name;          // "Varna", "Vashya", etc.
    private Integer obtained;     // points obtained
    private Integer maximum;      // max possible
    private String description;   // what this guna measures
    private String result;        // brief result text
}