package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.BookingResponse;
import com.ritvik.zodiacverseBackend.dto.CreateBookingRequest;
import com.ritvik.zodiacverseBackend.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        String email = authentication.getName();
        BookingResponse response = bookingService.create(email, request);

        return ResponseEntity.ok(
                ApiResponse.<BookingResponse>builder()
                        .message("Booking confirmed successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String email = authentication.getName();
        Page<BookingResponse> result = bookingService.list(email, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("bookings", result.getContent());
        data.put("currentPage", result.getNumber());
        data.put("totalPages", result.getTotalPages());
        data.put("totalItems", result.getTotalElements());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("Bookings fetched")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> get(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                ApiResponse.<BookingResponse>builder()
                        .message("Booking fetched")
                        .data(bookingService.get(email, id))
                        .build()
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                ApiResponse.<BookingResponse>builder()
                        .message("Booking cancelled and refunded")
                        .data(bookingService.cancel(email, id))
                        .build()
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> count(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Long>>builder()
                        .message("Count fetched")
                        .data(Map.of("count", bookingService.count(email)))
                        .build()
        );
    }
}