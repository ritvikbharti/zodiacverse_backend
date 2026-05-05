package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.CreateReportRequest;
import com.ritvik.zodiacverseBackend.dto.ReportResponse;
import com.ritvik.zodiacverseBackend.model.Report;
import com.ritvik.zodiacverseBackend.service.PdfGeneratorService;
import com.ritvik.zodiacverseBackend.service.ReportService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ReportService reportService;
    private final PdfGeneratorService pdfGenerator;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> generate(
            Authentication authentication,
            @Valid @RequestBody CreateReportRequest request
    ) {
        String email = authentication.getName();
        ReportResponse response = reportService.generate(email, request);
        return ResponseEntity.ok(
                ApiResponse.<ReportResponse>builder()
                        .message("Report generated successfully")
                        .data(response).build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String email = authentication.getName();
        Page<ReportResponse> result = reportService.list(email, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("reports", result.getContent());
        data.put("totalItems", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("Reports fetched").data(data).build()
        );
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        Report report = reportService.getEntity(email, id);
        byte[] pdf = pdfGenerator.generateFromHtml(report.getContent());

        String filename = report.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .body(pdf);
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<byte[]> view(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        Report report = reportService.getEntity(email, id);
        byte[] pdf = pdfGenerator.generateFromHtml(report.getContent());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"report.pdf\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .body(pdf);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        reportService.delete(authentication.getName(), id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder().message("Report deleted").data("DONE").build()
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> count(Authentication authentication) {
        long count = reportService.count(authentication.getName());
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Long>>builder()
                        .message("Count fetched")
                        .data(Map.of("count", count)).build()
        );
    }
}