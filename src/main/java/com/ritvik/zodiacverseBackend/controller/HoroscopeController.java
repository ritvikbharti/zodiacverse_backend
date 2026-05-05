package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.model.Horoscope;
import com.ritvik.zodiacverseBackend.repo.HoroscopeRepo;
import com.ritvik.zodiacverseBackend.service.HoroscopeGeneratorService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/horoscope")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class HoroscopeController {

    private final HoroscopeRepo horoscopeRepo;
    private final HoroscopeGeneratorService generatorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Horoscope>>> list(
            @RequestParam(defaultValue = "daily") String period
    ) {
        //  Use correct reference date for each period
        LocalDate periodDate = generatorService.getDateForPeriod(period);

        List<Horoscope> result = horoscopeRepo
                .findByPeriodAndHoroscopeDateOrderBySign(
                        period.toLowerCase(),
                        periodDate   // was always LocalDate.now() before
                );

        //  Auto-generate if missing (e.g., first request for this period)
        if (result.isEmpty()) {
            generatorService.generateForPeriod(period);
            result = horoscopeRepo.findByPeriodAndHoroscopeDateOrderBySign(
                    period.toLowerCase(), periodDate
            );
        }

        return ResponseEntity.ok(
                ApiResponse.<List<Horoscope>>builder()
                        .message("Horoscopes fetched")
                        .data(result)
                        .build()
        );
    }

    @GetMapping("/{sign}")
    public ResponseEntity<ApiResponse<Horoscope>> getBySign(
            @PathVariable String sign,
            @RequestParam(defaultValue = "daily") String period
    ) {
        //  Use correct reference date
        LocalDate periodDate = generatorService.getDateForPeriod(period);

        Horoscope h = horoscopeRepo
                .findBySignAndPeriodAndHoroscopeDate(
                        capitalize(sign),
                        period.toLowerCase(),
                        periodDate    // was LocalDate.now() before
                )
                .orElseThrow(() -> new RuntimeException(
                        "Horoscope not found for " + sign + " (" + period + ")"
                ));

        return ResponseEntity.ok(
                ApiResponse.<Horoscope>builder()
                        .message("Horoscope fetched")
                        .data(h)
                        .build()
        );
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}