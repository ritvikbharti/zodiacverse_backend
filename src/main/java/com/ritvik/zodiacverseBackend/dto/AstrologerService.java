package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.AstrologerResponse;
import com.ritvik.zodiacverseBackend.model.Astrologer;
import com.ritvik.zodiacverseBackend.repo.AstrologerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AstrologerService {

    private final AstrologerRepo astrologerRepo;

    public Page<AstrologerResponse> list(String query, String sortBy, int page, int size) {
        Sort sort = switch (sortBy == null ? "rating" : sortBy.toLowerCase()) {
            case "price_low" -> Sort.by("videoRate").ascending();
            case "price_high" -> Sort.by("videoRate").descending();
            case "experience" -> Sort.by("experienceYears").descending();
            default -> Sort.by("rating").descending();
        };

        return astrologerRepo
                .search(query == null || query.isBlank() ? null : query, PageRequest.of(page, size, sort))
                .map(this::toDto);
    }

    public AstrologerResponse get(UUID id) {
        Astrologer a = astrologerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Astrologer not found"));
        return toDto(a);
    }

    public AstrologerResponse toDto(Astrologer a) {
        return AstrologerResponse.builder()
                .id(a.getId())
                .name(a.getName())
                .bio(a.getBio())
                .specialties(splitCsv(a.getSpecialties()))
                .languages(splitCsv(a.getLanguages()))
                .experienceYears(a.getExperienceYears())
                .rating(a.getRating())
                .reviewsCount(a.getReviewsCount())
                .chatRate(a.getChatRate())
                .voiceRate(a.getVoiceRate())
                .videoRate(a.getVideoRate())
                .online(a.isOnline())
                .avatarUrl(a.getAvatarUrl())
                .build();
    }

    private List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}