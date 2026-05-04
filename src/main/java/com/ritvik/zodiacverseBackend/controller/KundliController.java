package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.CreateKundliRequest;
import com.ritvik.zodiacverseBackend.dto.KundliResponse;
import com.ritvik.zodiacverseBackend.dto.KundliSummaryResponse;
import com.ritvik.zodiacverseBackend.model.Kundli;
import com.ritvik.zodiacverseBackend.service.KundliService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kundli")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class KundliController {

    private final KundliService kundliService;

    @PostMapping
    public ResponseEntity<ApiResponse<KundliResponse>> createKundli(
            Authentication authentication,
            @Valid @RequestBody CreateKundliRequest request
    ) {
        String email = authentication.getName();
        Kundli k = kundliService.createKundli(email, request);

        // Return full response after creation
        KundliResponse response = kundliService.getKundli(email, k.getId());

        return ResponseEntity.ok(
                ApiResponse.<KundliResponse>builder()
                        .message("Kundli generated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> listKundlis(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();
        Page<KundliSummaryResponse> kundliPage = kundliService.listKundlis(email, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("kundlis", kundliPage.getContent());
        data.put("currentPage", kundliPage.getNumber());
        data.put("totalPages", kundliPage.getTotalPages());
        data.put("totalItems", kundliPage.getTotalElements());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("Kundlis fetched successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KundliResponse>> getKundli(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        KundliResponse response = kundliService.getKundli(email, id);

        return ResponseEntity.ok(
                ApiResponse.<KundliResponse>builder()
                        .message("Kundli fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteKundli(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        kundliService.deleteKundli(email, id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Kundli deleted successfully")
                        .data("DONE")
                        .build()
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> count(Authentication authentication) {
        String email = authentication.getName();
        long count = kundliService.getKundliCount(email);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Long>>builder()
                        .message("Count fetched")
                        .data(Map.of("count", count))
                        .build()
        );
    }
}