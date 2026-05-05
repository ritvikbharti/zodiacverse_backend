package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.CompatibilityRequest;
import com.ritvik.zodiacverseBackend.dto.CompatibilityResponse;
import com.ritvik.zodiacverseBackend.service.CompatibilityService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/compatibility")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CompatibilityController {

    private final CompatibilityService service;

    @PostMapping("/match")
    public ResponseEntity<ApiResponse<CompatibilityResponse>> match(
            Authentication authentication,
            @Valid @RequestBody CompatibilityRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                ApiResponse.<CompatibilityResponse>builder()
                        .message("Compatibility computed successfully")
                        .data(service.match(email, request))
                        .build()
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> history(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();
        Page<CompatibilityResponse> result = service.history(email, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("results", result.getContent());
        data.put("currentPage", result.getNumber());
        data.put("totalPages", result.getTotalPages());
        data.put("totalItems", result.getTotalElements());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("History fetched")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompatibilityResponse>> get(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                ApiResponse.<CompatibilityResponse>builder()
                        .message("Result fetched")
                        .data(service.getOne(email, id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        service.delete(email, id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Result deleted")
                        .data("DONE")
                        .build()
        );
    }
}