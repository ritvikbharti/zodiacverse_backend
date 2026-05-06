package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.AiChatRequest;
import com.ritvik.zodiacverseBackend.dto.AiChatResponse;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import com.ritvik.zodiacverseBackend.service.AiChatService;
import com.ritvik.zodiacverseBackend.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AiChatController {

    private final AiChatService aiChatService;
    private final UserRepo userRepo;
    private final RateLimiterService rateLimiter;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            Authentication authentication,
            @RequestBody AiChatRequest request) {

        String email = authentication.getName();

        //  Check rate limit BEFORE anything else
        if (!rateLimiter.isAIChatAllowed(email)) {
            return ResponseEntity.status(429).body(
                    AiChatResponse.builder()
                            .success(false)
                            .error("You've sent too many messages. " +
                                    "Please wait a minute 🌙")
                            .build()
            );
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            String reply = aiChatService.chat(request, user.getFullname());
            return ResponseEntity.ok(
                    AiChatResponse.builder()
                            .reply(reply)
                            .success(true)
                            .build()
            );
        } catch (Exception e) {
            //  Print full stack trace so we can see exactly what's failing
            log.error(" AI chat error type   : {}", e.getClass().getName());
            log.error(" AI chat error message: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error(" AI chat root cause  : {}", e.getCause().getMessage());
            }

            return ResponseEntity.status(503).body(
                    AiChatResponse.builder()
                            .success(false)
                            .error("The cosmic connection is disrupted. Try again shortly ✨")
                            .build()
            );
        }
    }
}