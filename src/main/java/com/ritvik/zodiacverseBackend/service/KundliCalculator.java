package com.ritvik.zodiacverseBackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritvik.zodiacverseBackend.model.Kundli;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class KundliCalculator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] SIGNS = {
            "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
            "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };

    private static final String[] RASHIS = {
            "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
            "Tula", "Vrishchika", "Dhanu", "Makara", "Kumbha", "Meena"
    };

    private static final String[] NAKSHATRAS = {
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
            "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni",
            "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha",
            "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha",
            "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada",
            "Uttara Bhadrapada", "Revati"
    };

    private static final String[] PLANETS = {
            "Sun", "Moon", "Mars", "Mercury", "Jupiter",
            "Venus", "Saturn", "Rahu", "Ketu"
    };

    /**
     * Calculate complete kundli from birth details.
     * NOTE: This is a deterministic, simplified calculator.
     * Replace with Swiss Ephemeris / Prokerala API for true accuracy.
     */
    public void compute(Kundli kundli) {
        LocalDate dob = kundli.getBirthDate();
        LocalTime tob = kundli.getBirthTime();

        // Sun sign — based purely on date (Western tropical)
        String sunSign = calculateSunSign(dob);
        kundli.setSunSign(sunSign);

        // Moon sign — pseudo-deterministic from date+time
        int moonIdx = computeMoonIndex(dob, tob);
        kundli.setMoonSign(SIGNS[moonIdx]);
        kundli.setRashi(RASHIS[moonIdx]);

        // Ascendant (Lagna) — based on time of day
        int ascIdx = computeAscendantIndex(dob, tob);
        kundli.setAscendant(SIGNS[ascIdx]);

        // Nakshatra — derived from moon position
        int nakIdx = computeNakshatraIndex(dob, tob);
        kundli.setNakshatra(NAKSHATRAS[nakIdx]);

        try {
            kundli.setPlanetaryPositionsJson(
                    objectMapper.writeValueAsString(generatePlanetaryPositions(dob, tob))
            );
            kundli.setHousesJson(
                    objectMapper.writeValueAsString(generateHouses(ascIdx))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize kundli data", e);
        }

        kundli.setPredictions(generatePredictions(sunSign, SIGNS[moonIdx], SIGNS[ascIdx]));
    }

    // ---------------- Sun sign by date ----------------
    private String calculateSunSign(LocalDate dob) {
        int day = dob.getDayOfMonth();
        int month = dob.getMonthValue();

        return switch (month) {
            case 1 -> day < 20 ? "Capricorn" : "Aquarius";
            case 2 -> day < 19 ? "Aquarius" : "Pisces";
            case 3 -> day < 21 ? "Pisces" : "Aries";
            case 4 -> day < 20 ? "Aries" : "Taurus";
            case 5 -> day < 21 ? "Taurus" : "Gemini";
            case 6 -> day < 21 ? "Gemini" : "Cancer";
            case 7 -> day < 23 ? "Cancer" : "Leo";
            case 8 -> day < 23 ? "Leo" : "Virgo";
            case 9 -> day < 23 ? "Virgo" : "Libra";
            case 10 -> day < 23 ? "Libra" : "Scorpio";
            case 11 -> day < 22 ? "Scorpio" : "Sagittarius";
            case 12 -> day < 22 ? "Sagittarius" : "Capricorn";
            default -> "Aries";
        };
    }

    // ---------------- Moon sign (pseudo) ----------------
    private int computeMoonIndex(LocalDate dob, LocalTime tob) {
        long days = dob.toEpochDay();
        // Moon moves through ~1 sign every 2.25 days
        int dayPart = (int) ((days / 2) % 12);
        int hourShift = (tob.getHour() / 6) % 4;
        return Math.floorMod(dayPart + hourShift, 12);
    }

    // ---------------- Ascendant (Lagna) ----------------
    private int computeAscendantIndex(LocalDate dob, LocalTime tob) {
        // Lagna changes every ~2 hours, full cycle per day
        int hour = tob.getHour();
        int dayOffset = (int) (dob.toEpochDay() % 12);
        int ascByHour = (hour / 2) % 12;
        return Math.floorMod(ascByHour + dayOffset, 12);
    }

    // ---------------- Nakshatra ----------------
    private int computeNakshatraIndex(LocalDate dob, LocalTime tob) {
        // 27 nakshatras, moon traverses ~1 per day
        long days = dob.toEpochDay();
        int byDate = (int) (days % 27);
        int byMinute = (tob.getHour() * 60 + tob.getMinute()) / 60;
        return Math.floorMod(byDate + byMinute, 27);
    }

    // ---------------- Planet positions ----------------
    private List<Map<String, Object>> generatePlanetaryPositions(LocalDate dob, LocalTime tob) {
        List<Map<String, Object>> list = new ArrayList<>();
        long seed = dob.toEpochDay() * 31L + tob.toSecondOfDay();
        Random rand = new Random(seed);

        for (int i = 0; i < PLANETS.length; i++) {
            String planet = PLANETS[i];
            int signIdx = (int) Math.floorMod(seed / (i + 1), 12);
            double degree = 1 + rand.nextDouble() * 28;          // 1..29
            int house = (int) Math.floorMod(seed / (i + 2), 12) + 1; // 1..12
            boolean retrograde = (i > 0 && i < 7) && rand.nextDouble() < 0.2;

            Map<String, Object> p = new LinkedHashMap<>();
            p.put("planet", planet);
            p.put("sign", SIGNS[signIdx]);
            p.put("degree", Math.round(degree * 100.0) / 100.0);
            p.put("house", house);
            p.put("retrograde", retrograde);
            list.add(p);
        }
        return list;
    }

    // ---------------- Houses ----------------
    private List<Map<String, Object>> generateHouses(int ascIdx) {
        List<Map<String, Object>> houses = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> h = new LinkedHashMap<>();
            h.put("house", i + 1);
            h.put("sign", SIGNS[Math.floorMod(ascIdx + i, 12)]);
            h.put("meaning", HOUSE_MEANINGS[i]);
            houses.add(h);
        }
        return houses;
    }

    private static final String[] HOUSE_MEANINGS = {
            "Self, personality, appearance",
            "Wealth, family, speech",
            "Siblings, courage, communication",
            "Home, mother, comforts",
            "Children, creativity, intellect",
            "Health, enemies, service",
            "Marriage, partnerships",
            "Longevity, transformation",
            "Fortune, dharma, higher learning",
            "Career, status, reputation",
            "Gains, friendships, aspirations",
            "Losses, expenses, spirituality"
    };

    // ---------------- Predictions ----------------
    private String generatePredictions(String sun, String moon, String asc) {
        return String.format(
                "With your Sun in %s, you carry the spirit of confidence and self-expression. " +
                        "Your Moon in %s shapes your emotional landscape with depth and intuition. " +
                        "An Ascendant of %s gives you a distinctive presence and approach to life. " +
                        "The cosmos suggests focusing on personal growth and meaningful relationships in the coming months. " +
                        "Your unique planetary alignment points toward opportunities in creative pursuits and spiritual exploration.",
                sun, moon, asc
        );
    }
}