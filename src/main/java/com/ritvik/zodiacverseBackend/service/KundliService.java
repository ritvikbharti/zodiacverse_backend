package com.ritvik.zodiacverseBackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritvik.zodiacverseBackend.dto.CreateKundliRequest;
import com.ritvik.zodiacverseBackend.dto.KundliResponse;
import com.ritvik.zodiacverseBackend.dto.KundliSummaryResponse;
import com.ritvik.zodiacverseBackend.model.Gender;
import com.ritvik.zodiacverseBackend.model.Kundli;
import com.ritvik.zodiacverseBackend.model.KundliSystem;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.KundliRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KundliService {

    private final KundliRepo kundliRepo;
    private final UserRepo userRepo;
    private final KundliCalculator kundliCalculator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Kundli createKundli(String email, CreateKundliRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Gender gender;
        try {
            gender = Gender.valueOf(req.getGender().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid gender. Use MALE, FEMALE, or OTHER");
        }

        KundliSystem system = KundliSystem.VEDIC;
        if (req.getSystem() != null && !req.getSystem().isBlank()) {
            try {
                system = KundliSystem.valueOf(req.getSystem().toUpperCase());
            } catch (Exception e) {
                throw new RuntimeException("Invalid system. Use VEDIC, WESTERN, or KP");
            }
        }

        Kundli kundli = Kundli.builder()
                .user(user)
                .personName(req.getPersonName())
                .gender(gender)
                .birthDate(req.getBirthDate())
                .birthTime(req.getBirthTime())
                .birthPlace(req.getBirthPlace())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .timezone(req.getTimezone() != null ? req.getTimezone() : "Asia/Kolkata")
                .system(system)
                .build();

        kundliCalculator.compute(kundli);

        return kundliRepo.save(kundli);
    }

    public Page<KundliSummaryResponse> listKundlis(String email, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Kundli> kundlis = kundliRepo.findByUserIdOrderByCreatedAtDesc(
                user.getId(),
                PageRequest.of(page, size)
        );

        return kundlis.map(this::toSummary);
    }

    public KundliResponse getKundli(String email, UUID kundliId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Kundli kundli = kundliRepo.findByIdAndUserId(kundliId, user.getId())
                .orElseThrow(() -> new RuntimeException("Kundli not found"));

        return toFullResponse(kundli);
    }

    @Transactional
    public void deleteKundli(String email, UUID kundliId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Kundli kundli = kundliRepo.findByIdAndUserId(kundliId, user.getId())
                .orElseThrow(() -> new RuntimeException("Kundli not found"));

        kundliRepo.delete(kundli);
    }

    public long getKundliCount(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return kundliRepo.countByUserId(user.getId());
    }

    // ---------- Mappers ----------
    private KundliSummaryResponse toSummary(Kundli k) {
        return KundliSummaryResponse.builder()
                .id(k.getId())
                .personName(k.getPersonName())
                .birthDate(k.getBirthDate())
                .birthPlace(k.getBirthPlace())
                .sunSign(k.getSunSign())
                .moonSign(k.getMoonSign())
                .ascendant(k.getAscendant())
                .system(k.getSystem().name())
                .createdAt(k.getCreatedAt())
                .build();
    }

    private KundliResponse toFullResponse(Kundli k) {
        List<Map<String, Object>> planets = parseJsonList(k.getPlanetaryPositionsJson());
        List<Map<String, Object>> houses = parseJsonList(k.getHousesJson());

        return KundliResponse.builder()
                .id(k.getId())
                .personName(k.getPersonName())
                .gender(k.getGender().name())
                .birthDate(k.getBirthDate())
                .birthTime(k.getBirthTime())
                .birthPlace(k.getBirthPlace())
                .latitude(k.getLatitude())
                .longitude(k.getLongitude())
                .timezone(k.getTimezone())
                .system(k.getSystem().name())
                .sunSign(k.getSunSign())
                .moonSign(k.getMoonSign())
                .ascendant(k.getAscendant())
                .nakshatra(k.getNakshatra())
                .rashi(k.getRashi())
                .planetaryPositions(planets)
                .houses(houses)
                .predictions(k.getPredictions())
                .createdAt(k.getCreatedAt())
                .build();
    }

    private List<Map<String, Object>> parseJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}