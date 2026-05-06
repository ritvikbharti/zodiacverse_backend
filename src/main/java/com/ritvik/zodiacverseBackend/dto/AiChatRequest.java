package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatRequest {

    // Current user message
    private String message;

    // Full conversation history for context
    private List<ChatMessageDto> history;

    // Optional — user's sun sign for personalization
    private String sunSign;
}