package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.AiChatRequest;
import com.ritvik.zodiacverseBackend.dto.AiChatRequest;
import com.ritvik.zodiacverseBackend.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.openai.api-key}")
    private String apiKey;

    @Value("${app.openai.model:gpt-4o-mini}")
    private String model;

    @Value("${app.openai.max-tokens:500}")
    private int maxTokens;

    @Value("${app.openai.temperature:0.7}")
    private double temperature;

    private static final String OPENAI_URL =
            "https://api.groq.com/openai/v1/chat/completions";
    private static final String CACHE_PREFIX = "ai:chat:";
    private static final Duration CACHE_TTL   = Duration.ofHours(1);

    public String chat(AiChatRequest request, String userFullName) {

        // ── Build cache key from message for common questions ─
        String cacheKey = CACHE_PREFIX + request.getMessage()
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "_")
                .substring(0, Math.min(50,
                        request.getMessage().length()));

        // ✅ Check Redis cache — avoid repeated API calls for same questions
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null && (request.getHistory() == null
                || request.getHistory().isEmpty())) {
            log.info("⚡ AI cache hit for: {}", request.getMessage());
            return cached.toString();
        }

        // ── Build messages array for OpenAI ───────────────────
        List<Map<String, String>> messages = new ArrayList<>();

        // System prompt — personalized astrology assistant
        messages.add(Map.of(
                "role", "system",
                "content", buildSystemPrompt(userFullName, request.getSunSign())
        ));

        // Conversation history (last 10 messages max to save tokens)
        if (request.getHistory() != null) {
            int start = Math.max(0, request.getHistory().size() - 10);
            for (int i = start; i < request.getHistory().size(); i++) {
                ChatMessageDto msg = request.getHistory().get(i);
                messages.add(Map.of(
                        "role",    msg.getRole(),
                        "content", msg.getContent()
                ));
            }
        }

        // Current user message
        messages.add(Map.of("role", "user", "content", request.getMessage()));

        // ── Call OpenAI ───────────────────────────────────────
        Map<String, Object> body = new HashMap<>();
        body.put("model",       model);
        body.put("messages",    messages);
        body.put("max_tokens",  maxTokens);
        body.put("temperature", temperature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> httpRequest =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    OPENAI_URL, httpRequest, Map.class);

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null) {

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices =
                        (List<Map<String, Object>>) response.getBody().get("choices");

                @SuppressWarnings("unchecked")
                Map<String, String> message =
                        (Map<String, String>) choices.get(0).get("message");

                String reply = message.get("content");

                if (request.getHistory() == null || request.getHistory().isEmpty()) {
                    redisTemplate.opsForValue().set(cacheKey, reply, CACHE_TTL);
                }

                log.info("✅ AI reply generated for: {}", userFullName);
                return reply;
            }

            // ✅ Log unexpected response
            log.error("❌ OpenAI unexpected status: {}", response.getStatusCode());
            log.error("❌ OpenAI response body    : {}", response.getBody());
            return "The stars are momentarily clouded. Please try again 🌙";

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // 4xx from OpenAI — invalid key, quota exceeded etc.
            log.error("❌ OpenAI client error: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API error: " + e.getStatusCode());

        } catch (org.springframework.web.client.HttpServerErrorException e) {
            // 5xx from OpenAI
            log.error("❌ OpenAI server error: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI server error");

        } catch (org.springframework.web.client.ResourceAccessException e) {
            // Network/timeout error
            log.error("❌ OpenAI network error: {}", e.getMessage());
            throw new RuntimeException("Cannot reach OpenAI — check internet/firewall");

        } catch (Exception e) {
            log.error("❌ OpenAI unknown error: {} — {}", e.getClass().getName(), e.getMessage());
            throw new RuntimeException("AI service error: " + e.getMessage());
        }
    }

    // ── System prompt ─────────────────────────────────────────
    private String buildSystemPrompt(String userName, String sunSign) {
        String sign = (sunSign != null && !sunSign.isBlank())
                ? sunSign
                : "unknown (suggest the user generates a kundli)";

        return String.format("""
            You are Cosmo ✨, an expert AI astrology assistant for ZodiacVerse.
            You are wise, warm, mystical, and deeply knowledgeable about Vedic
            and Western astrology.

            Current user : %s
            Sun Sign     : %s
            Today's date : %s

            You can help with:
            - Explaining astrological concepts (planets, houses, aspects)
            - Interpreting birth charts and kundli readings
            - Daily, weekly, and monthly horoscope insights
            - Planetary transits and retrograde effects
            - Compatibility and synastry analysis
            - Numerology and lucky numbers
            - Guiding users through ZodiacVerse features
              (kundli generation, booking astrologers, reports)

            Rules:
            - Keep responses to 2-4 sentences unless the user asks for detail
            - Be warm, encouraging, and mystical in tone
            - Use cosmic emojis (✨ 🌙 🔮 ⭐ 🪐) sparingly — max 2 per response
            - Always personalize using the user's name and sun sign when relevant
            - If asked about features, guide them to the correct section of the app
            - Never make definitive predictions about health, finance, or relationships
            - If you don't know something, say so gracefully
            """,
                userName != null ? userName : "Cosmic Traveller",
                sign,
                LocalDate.now()
        );
    }
}