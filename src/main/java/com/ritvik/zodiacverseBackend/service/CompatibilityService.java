package com.ritvik.zodiacverseBackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritvik.zodiacverseBackend.dto.CompatibilityRequest;
import com.ritvik.zodiacverseBackend.dto.CompatibilityResponse;
import com.ritvik.zodiacverseBackend.dto.GunaScore;
import com.ritvik.zodiacverseBackend.model.Compatibility;
import com.ritvik.zodiacverseBackend.model.Kundli;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.CompatibilityRepo;
import com.ritvik.zodiacverseBackend.repo.KundliRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompatibilityService {

    private final CompatibilityRepo compatibilityRepo;
    private final KundliRepo kundliRepo;
    private final UserRepo userRepo;
    private final AshtakutaCalculator ashtakuta;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CompatibilityResponse match(String email, CompatibilityRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getKundli1Id().equals(req.getKundli2Id())) {
            throw new RuntimeException("Cannot match a kundli with itself");
        }

        Kundli k1 = kundliRepo.findByIdAndUserId(req.getKundli1Id(), user.getId())
                .orElseThrow(() -> new RuntimeException("Kundli 1 not found or not yours"));

        Kundli k2 = kundliRepo.findByIdAndUserId(req.getKundli2Id(), user.getId())
                .orElseThrow(() -> new RuntimeException("Kundli 2 not found or not yours"));

        List<GunaScore> breakdown = ashtakuta.compute(k1, k2);
        int total = ashtakuta.totalOf(breakdown);

        String verdictLevel = verdictLevel(total);
        String verdict = verdictText(total, verdictLevel);
        String strengths = computeStrengths(breakdown);
        String challenges = computeChallenges(breakdown);
        String recommendation = computeRecommendation(breakdown, total);

        Compatibility entity;
        try {
            entity = Compatibility.builder()
                    .user(user)
                    .kundli1(k1)
                    .kundli2(k2)
                    .totalScore(total)
                    .breakdownJson(objectMapper.writeValueAsString(breakdown))
                    .verdict(verdict)
                    .strengths(strengths)
                    .challenges(challenges)
                    .recommendation(recommendation)
                    .build();
            compatibilityRepo.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save compatibility result", e);
        }

        return CompatibilityResponse.builder()
                .id(entity.getId())
                .kundli1Id(k1.getId()).person1Name(k1.getPersonName())
                .kundli2Id(k2.getId()).person2Name(k2.getPersonName())
                .totalScore(total).maxScore(36)
                .percentage((int) Math.round(total / 36.0 * 100))
                .verdict(verdict).verdictLevel(verdictLevel)
                .breakdown(breakdown)
                .strengths(strengths).challenges(challenges).recommendation(recommendation)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Page<CompatibilityResponse> history(String email, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return compatibilityRepo
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public CompatibilityResponse getOne(String email, UUID id) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Compatibility c = compatibilityRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Result not found"));
        return toResponse(c);
    }

    @Transactional
    public void delete(String email, UUID id) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Compatibility c = compatibilityRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Result not found"));
        compatibilityRepo.delete(c);
    }

    // ---------- helpers ----------
    private CompatibilityResponse toResponse(Compatibility c) {
        List<GunaScore> breakdown = parseBreakdown(c.getBreakdownJson());
        return CompatibilityResponse.builder()
                .id(c.getId())
                .kundli1Id(c.getKundli1().getId()).person1Name(c.getKundli1().getPersonName())
                .kundli2Id(c.getKundli2().getId()).person2Name(c.getKundli2().getPersonName())
                .totalScore(c.getTotalScore()).maxScore(36)
                .percentage((int) Math.round(c.getTotalScore() / 36.0 * 100))
                .verdict(c.getVerdict()).verdictLevel(verdictLevel(c.getTotalScore()))
                .breakdown(breakdown)
                .strengths(c.getStrengths()).challenges(c.getChallenges())
                .recommendation(c.getRecommendation())
                .createdAt(c.getCreatedAt()).build();
    }

    private List<GunaScore> parseBreakdown(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String verdictLevel(int total) {
        if (total >= 32) return "EXCELLENT";
        if (total >= 24) return "GOOD";
        if (total >= 18) return "AVERAGE";
        return "POOR";
    }

    private String verdictText(int total, String level) {
        return switch (level) {
            case "EXCELLENT" -> "🌟 A cosmic match made in heaven! Your stars align in remarkable harmony, " +
                    "promising a blissful and prosperous union.";
            case "GOOD" -> "✨ A beautiful connection! Strong compatibility across most areas — " +
                    "this is a recommended match with great potential.";
            case "AVERAGE" -> "🌙 An average match — workable with mutual effort. Some areas need attention, " +
                    "but love and understanding can bridge the gaps.";
            default -> "⚠️ Several incompatibilities detected. Traditional astrology recommends caution and " +
                    "remedial measures before proceeding.";
        };
    }

    private String computeStrengths(List<GunaScore> breakdown) {
        StringBuilder sb = new StringBuilder();
        breakdown.stream()
                .filter(g -> g.getObtained() >= g.getMaximum() * 0.75)
                .forEach(g -> sb.append("• ").append(g.getName()).append(": ").append(g.getResult()).append("\n"));
        return sb.length() == 0 ? "Most gunas need attention — see breakdown for details." : sb.toString().trim();
    }

    private String computeChallenges(List<GunaScore> breakdown) {
        StringBuilder sb = new StringBuilder();
        breakdown.stream()
                .filter(g -> g.getObtained() < g.getMaximum() * 0.4)
                .forEach(g -> sb.append("• ").append(g.getName()).append(": ").append(g.getResult()).append("\n"));
        return sb.length() == 0 ? "No major challenges detected. Smooth sailing ahead! ✨" : sb.toString().trim();
    }

    private String computeRecommendation(List<GunaScore> breakdown, int total) {
        if (total >= 24) {
            return "Highly recommended union. Consider auspicious dates for any commitments. " +
                    "Practice mutual gratitude meditation to strengthen the bond.";
        }
        boolean nadiDosha = breakdown.stream()
                .anyMatch(g -> g.getName().equals("Nadi") && g.getObtained() == 0);
        boolean bhakootDosha = breakdown.stream()
                .anyMatch(g -> g.getName().equals("Bhakoot") && g.getObtained() == 0);

        StringBuilder sb = new StringBuilder("Suggested remedies:\n");
        if (nadiDosha) sb.append("• Perform Mahamrityunjaya Jaap for health and progeny\n");
        if (bhakootDosha) sb.append("• Donate to a temple on Saturdays\n");
        sb.append("• Wear matching gemstones based on each person's lagna\n");
        sb.append("• Consult an astrologer for personalized remedies");
        return sb.toString();
    }
}