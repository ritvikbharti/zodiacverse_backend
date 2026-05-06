package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String role;    // "user" or "assistant"
    private String content;
}