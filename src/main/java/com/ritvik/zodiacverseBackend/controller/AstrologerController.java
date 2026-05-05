package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.AstrologerResponse;
import com.ritvik.zodiacverseBackend.service.AstrologerService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/astrologers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AstrologerController {

    private final AstrologerService astrologerService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<AstrologerResponse> result = astrologerService.list(q, sortBy, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("astrologers", result.getContent());
        data.put("currentPage", result.getNumber());
        data.put("totalPages", result.getTotalPages());
        data.put("totalItems", result.getTotalElements());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("Astrologers fetched")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AstrologerResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.<AstrologerResponse>builder()
                        .message("Astrologer fetched")
                        .data(astrologerService.get(id))
                        .build()
        );
    }
}