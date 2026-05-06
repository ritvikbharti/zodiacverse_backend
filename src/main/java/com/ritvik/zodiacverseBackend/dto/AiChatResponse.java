package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatResponse {
    private String reply;
    private boolean success;
    private String error;
}