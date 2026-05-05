package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.GunaScore;
import com.ritvik.zodiacverseBackend.model.Kundli;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AshtakutaCalculator {

    // 27 Nakshatras
    private static final String[] NAKSHATRAS = {
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
            "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni",
            "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha",
            "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha",
            "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada",
            "Uttara Bhadrapada", "Revati"
    };

    // 12 Rashis (Moon signs)
    private static final String[] RASHIS = {
            "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
            "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };

    // Yoni mapping (animal symbol per nakshatra)
    private static final Map<String, String> NAKSHATRA_TO_YONI = Map.ofEntries(
            Map.entry("Ashwini", "Horse"), Map.entry("Bharani", "Elephant"),
            Map.entry("Krittika", "Sheep"), Map.entry("Rohini", "Serpent"),
            Map.entry("Mrigashira", "Serpent"), Map.entry("Ardra", "Dog"),
            Map.entry("Punarvasu", "Cat"), Map.entry("Pushya", "Sheep"),
            Map.entry("Ashlesha", "Cat"), Map.entry("Magha", "Rat"),
            Map.entry("Purva Phalguni", "Rat"), Map.entry("Uttara Phalguni", "Cow"),
            Map.entry("Hasta", "Buffalo"), Map.entry("Chitra", "Tiger"),
            Map.entry("Swati", "Buffalo"), Map.entry("Vishakha", "Tiger"),
            Map.entry("Anuradha", "Deer"), Map.entry("Jyeshtha", "Deer"),
            Map.entry("Mula", "Dog"), Map.entry("Purva Ashadha", "Monkey"),
            Map.entry("Uttara Ashadha", "Mongoose"), Map.entry("Shravana", "Monkey"),
            Map.entry("Dhanishta", "Lion"), Map.entry("Shatabhisha", "Horse"),
            Map.entry("Purva Bhadrapada", "Lion"),
            Map.entry("Uttara Bhadrapada", "Cow"), Map.entry("Revati", "Elephant")
    );

    // Gana per nakshatra (Deva, Manushya, Rakshasa)
    private static final Map<String, String> NAKSHATRA_TO_GANA = Map.ofEntries(
            Map.entry("Ashwini", "Deva"), Map.entry("Bharani", "Manushya"),
            Map.entry("Krittika", "Rakshasa"), Map.entry("Rohini", "Manushya"),
            Map.entry("Mrigashira", "Deva"), Map.entry("Ardra", "Manushya"),
            Map.entry("Punarvasu", "Deva"), Map.entry("Pushya", "Deva"),
            Map.entry("Ashlesha", "Rakshasa"), Map.entry("Magha", "Rakshasa"),
            Map.entry("Purva Phalguni", "Manushya"), Map.entry("Uttara Phalguni", "Manushya"),
            Map.entry("Hasta", "Deva"), Map.entry("Chitra", "Rakshasa"),
            Map.entry("Swati", "Deva"), Map.entry("Vishakha", "Rakshasa"),
            Map.entry("Anuradha", "Deva"), Map.entry("Jyeshtha", "Rakshasa"),
            Map.entry("Mula", "Rakshasa"), Map.entry("Purva Ashadha", "Manushya"),
            Map.entry("Uttara Ashadha", "Manushya"), Map.entry("Shravana", "Deva"),
            Map.entry("Dhanishta", "Rakshasa"), Map.entry("Shatabhisha", "Rakshasa"),
            Map.entry("Purva Bhadrapada", "Manushya"), Map.entry("Uttara Bhadrapada", "Manushya"),
            Map.entry("Revati", "Deva")
    );

    // Nadi per nakshatra (Adi, Madhya, Antya)
    private static final Map<String, String> NAKSHATRA_TO_NADI = Map.ofEntries(
            Map.entry("Ashwini", "Adi"), Map.entry("Bharani", "Madhya"), Map.entry("Krittika", "Antya"),
            Map.entry("Rohini", "Antya"), Map.entry("Mrigashira", "Madhya"), Map.entry("Ardra", "Adi"),
            Map.entry("Punarvasu", "Adi"), Map.entry("Pushya", "Madhya"), Map.entry("Ashlesha", "Antya"),
            Map.entry("Magha", "Antya"), Map.entry("Purva Phalguni", "Madhya"), Map.entry("Uttara Phalguni", "Adi"),
            Map.entry("Hasta", "Adi"), Map.entry("Chitra", "Madhya"), Map.entry("Swati", "Antya"),
            Map.entry("Vishakha", "Antya"), Map.entry("Anuradha", "Madhya"), Map.entry("Jyeshtha", "Adi"),
            Map.entry("Mula", "Adi"), Map.entry("Purva Ashadha", "Madhya"), Map.entry("Uttara Ashadha", "Antya"),
            Map.entry("Shravana", "Antya"), Map.entry("Dhanishta", "Madhya"), Map.entry("Shatabhisha", "Adi"),
            Map.entry("Purva Bhadrapada", "Adi"), Map.entry("Uttara Bhadrapada", "Madhya"),
            Map.entry("Revati", "Antya")
    );

    // Varna per rashi (Brahmin, Kshatriya, Vaishya, Shudra)
    private static final Map<String, Integer> RASHI_TO_VARNA = Map.ofEntries(
            Map.entry("Cancer", 4), Map.entry("Scorpio", 4), Map.entry("Pisces", 4),  // Brahmin
            Map.entry("Aries", 3), Map.entry("Leo", 3), Map.entry("Sagittarius", 3),  // Kshatriya
            Map.entry("Taurus", 2), Map.entry("Virgo", 2), Map.entry("Capricorn", 2), // Vaishya
            Map.entry("Gemini", 1), Map.entry("Libra", 1), Map.entry("Aquarius", 1)   // Shudra
    );

    // Vashya per rashi
    private static final Map<String, String> RASHI_TO_VASHYA = Map.ofEntries(
            Map.entry("Aries", "Chatushpada"), Map.entry("Taurus", "Chatushpada"),
            Map.entry("Gemini", "Manava"), Map.entry("Cancer", "Jalachara"),
            Map.entry("Leo", "Vanachara"), Map.entry("Virgo", "Manava"),
            Map.entry("Libra", "Manava"), Map.entry("Scorpio", "Keeta"),
            Map.entry("Sagittarius", "Chatushpada"), Map.entry("Capricorn", "Jalachara"),
            Map.entry("Aquarius", "Manava"), Map.entry("Pisces", "Jalachara")
    );

    /**
     * Compute full Ashtakuta compatibility score.
     */
    public List<GunaScore> compute(Kundli k1, Kundli k2) {
        List<GunaScore> scores = new ArrayList<>();
        scores.add(computeVarna(k1, k2));
        scores.add(computeVashya(k1, k2));
        scores.add(computeTara(k1, k2));
        scores.add(computeYoni(k1, k2));
        scores.add(computeGrahaMaitri(k1, k2));
        scores.add(computeGana(k1, k2));
        scores.add(computeBhakoot(k1, k2));
        scores.add(computeNadi(k1, k2));
        return scores;
    }

    public int totalOf(List<GunaScore> scores) {
        return scores.stream().mapToInt(GunaScore::getObtained).sum();
    }

    // ==================== 1. VARNA (1 pt) ====================
    private GunaScore computeVarna(Kundli k1, Kundli k2) {
        int v1 = RASHI_TO_VARNA.getOrDefault(k1.getMoonSign(), 1);
        int v2 = RASHI_TO_VARNA.getOrDefault(k2.getMoonSign(), 1);
        int pts = (v1 >= v2) ? 1 : 0;
        return GunaScore.builder()
                .name("Varna")
                .obtained(pts)
                .maximum(1)
                .description("Spiritual & ego compatibility — measures the alignment of inner natures")
                .result(pts == 1
                        ? "Compatible — egos align harmoniously"
                        : "Slight mismatch — work on mutual respect")
                .build();
    }

    // ==================== 2. VASHYA (2 pts) ====================
    private GunaScore computeVashya(Kundli k1, Kundli k2) {
        String v1 = RASHI_TO_VASHYA.getOrDefault(k1.getMoonSign(), "Manava");
        String v2 = RASHI_TO_VASHYA.getOrDefault(k2.getMoonSign(), "Manava");

        int pts;
        if (v1.equals(v2)) pts = 2;
        else if (v1.equals("Manava") || v2.equals("Manava")) pts = 1;
        else pts = 0;

        return GunaScore.builder()
                .name("Vashya")
                .obtained(pts)
                .maximum(2)
                .description("Mutual attraction & control between partners")
                .result(scoreText(pts, 2,
                        "Strong magnetic attraction",
                        "Moderate attraction",
                        "Need conscious effort to bond"))
                .build();
    }

    // ==================== 3. TARA (3 pts) ====================
    private GunaScore computeTara(Kundli k1, Kundli k2) {
        int n1 = nakshatraIndex(k1.getNakshatra());
        int n2 = nakshatraIndex(k2.getNakshatra());
        if (n1 < 0 || n2 < 0) return defaultScore("Tara", 1, 3, "Unknown nakshatra");

        // Count from each
        int taraFrom1 = ((n2 - n1 + 27) % 27) + 1;
        int taraFrom2 = ((n1 - n2 + 27) % 27) + 1;

        // 3, 5, 7 are inauspicious
        boolean ok1 = !isBadTara(taraFrom1);
        boolean ok2 = !isBadTara(taraFrom2);

        int pts = (ok1 && ok2) ? 3 : (ok1 || ok2 ? 1 : 0);

        return GunaScore.builder()
                .name("Tara")
                .obtained(pts)
                .maximum(3)
                .description("Birth star compatibility — health, longevity, well-being")
                .result(scoreText(pts, 3,
                        "Auspicious stars — good health and prosperity together",
                        "Mixed influence — some areas need care",
                        "Stars need remedies for harmony"))
                .build();
    }

    private boolean isBadTara(int taraNum) {
        int rem = ((taraNum - 1) % 9) + 1;
        return rem == 3 || rem == 5 || rem == 7;
    }

    // ==================== 4. YONI (4 pts) ====================
    private GunaScore computeYoni(Kundli k1, Kundli k2) {
        String y1 = NAKSHATRA_TO_YONI.getOrDefault(k1.getNakshatra(), "Horse");
        String y2 = NAKSHATRA_TO_YONI.getOrDefault(k2.getNakshatra(), "Horse");

        int pts;
        if (y1.equals(y2)) pts = 4;                                  // same yoni
        else if (areYoniFriendly(y1, y2)) pts = 3;
        else if (areYoniNeutral(y1, y2)) pts = 2;
        else if (areYoniEnemy(y1, y2)) pts = 0;
        else pts = 1;

        return GunaScore.builder()
                .name("Yoni")
                .obtained(pts)
                .maximum(4)
                .description("Sexual & physical compatibility")
                .result(scoreText(pts, 4,
                        "Strong physical & intimate connection",
                        "Decent physical chemistry",
                        "Physical bonding requires effort"))
                .build();
    }

    private boolean areYoniFriendly(String a, String b) {
        return (a.equals("Cow") && b.equals("Tiger")) || (a.equals("Tiger") && b.equals("Cow"))
                || (a.equals("Horse") && b.equals("Buffalo")) || (a.equals("Buffalo") && b.equals("Horse"));
    }

    private boolean areYoniNeutral(String a, String b) {
        return a.equals("Deer") && b.equals("Sheep") || b.equals("Deer") && a.equals("Sheep");
    }

    private boolean areYoniEnemy(String a, String b) {
        return (a.equals("Cat") && b.equals("Rat")) || (a.equals("Rat") && b.equals("Cat"))
                || (a.equals("Dog") && b.equals("Deer")) || (a.equals("Deer") && b.equals("Dog"))
                || (a.equals("Serpent") && b.equals("Mongoose")) || (a.equals("Mongoose") && b.equals("Serpent"));
    }

    // ==================== 5. GRAHA MAITRI (5 pts) ====================
    private GunaScore computeGrahaMaitri(Kundli k1, Kundli k2) {
        // Friendship between moon-sign lords
        String r1 = k1.getMoonSign();
        String r2 = k2.getMoonSign();

        int pts;
        if (r1.equals(r2)) pts = 5;
        else if (areLordsFriends(r1, r2)) pts = 4;
        else if (areLordsNeutral(r1, r2)) pts = 3;
        else pts = 0;

        return GunaScore.builder()
                .name("Graha Maitri")
                .obtained(pts)
                .maximum(5)
                .description("Mental & intellectual compatibility — friendship of moon-sign lords")
                .result(scoreText(pts, 5,
                        "Excellent mental harmony — deep understanding",
                        "Decent intellectual rapport",
                        "Mental wavelengths differ — communication is key"))
                .build();
    }

    // Simplified ruler friendship
    private boolean areLordsFriends(String r1, String r2) {
        // Same element = friendly
        return element(r1).equals(element(r2));
    }

    private boolean areLordsNeutral(String r1, String r2) {
        String e1 = element(r1), e2 = element(r2);
        return (e1.equals("Fire") && e2.equals("Air")) || (e1.equals("Air") && e2.equals("Fire"))
                || (e1.equals("Earth") && e2.equals("Water")) || (e1.equals("Water") && e2.equals("Earth"));
    }

    private String element(String rashi) {
        return switch (rashi) {
            case "Aries", "Leo", "Sagittarius" -> "Fire";
            case "Taurus", "Virgo", "Capricorn" -> "Earth";
            case "Gemini", "Libra", "Aquarius" -> "Air";
            case "Cancer", "Scorpio", "Pisces" -> "Water";
            default -> "Unknown";
        };
    }

    // ==================== 6. GANA (6 pts) ====================
    private GunaScore computeGana(Kundli k1, Kundli k2) {
        String g1 = NAKSHATRA_TO_GANA.getOrDefault(k1.getNakshatra(), "Manushya");
        String g2 = NAKSHATRA_TO_GANA.getOrDefault(k2.getNakshatra(), "Manushya");

        int pts;
        if (g1.equals(g2)) pts = 6;
        else if (g1.equals("Deva") && g2.equals("Manushya") || g1.equals("Manushya") && g2.equals("Deva")) pts = 5;
        else if (g1.equals("Manushya") && g2.equals("Rakshasa") || g1.equals("Rakshasa") && g2.equals("Manushya")) pts = 1;
        else pts = 0;   // Deva-Rakshasa

        return GunaScore.builder()
                .name("Gana")
                .obtained(pts)
                .maximum(6)
                .description("Temperament compatibility — divine/human/demonic natures")
                .result(scoreText(pts, 6,
                        "Beautiful temperament match — natural harmony",
                        "Workable temperaments — minor adjustments needed",
                        "Different worlds — need patience and understanding"))
                .build();
    }

    // ==================== 7. BHAKOOT (7 pts) ====================
    private GunaScore computeBhakoot(Kundli k1, Kundli k2) {
        int r1 = rashiIndex(k1.getMoonSign());
        int r2 = rashiIndex(k2.getMoonSign());
        if (r1 < 0 || r2 < 0) return defaultScore("Bhakoot", 0, 7, "Unknown rashi");

        int diff = Math.abs(r1 - r2);
        int rel = diff + 1;            // 1-12

        // Inauspicious: 6/8, 9/5, 12/2
        boolean inauspicious = rel == 6 || rel == 8 || rel == 5 || rel == 9 || rel == 2 || rel == 12;
        int pts = inauspicious ? 0 : 7;

        return GunaScore.builder()
                .name("Bhakoot")
                .obtained(pts)
                .maximum(7)
                .description("Family welfare, financial growth & emotional bonding")
                .result(pts == 7
                        ? "Excellent — promotes family prosperity & love"
                        : "Some financial/emotional friction — requires remedies")
                .build();
    }

    // ==================== 8. NADI (8 pts) ====================
    private GunaScore computeNadi(Kundli k1, Kundli k2) {
        String n1 = NAKSHATRA_TO_NADI.getOrDefault(k1.getNakshatra(), "Adi");
        String n2 = NAKSHATRA_TO_NADI.getOrDefault(k2.getNakshatra(), "Adi");

        int pts = n1.equals(n2) ? 0 : 8;     // Same Nadi = 0 (Nadi Dosha)

        return GunaScore.builder()
                .name("Nadi")
                .obtained(pts)
                .maximum(8)
                .description("Health, genetic & progeny compatibility (most important!)")
                .result(pts == 8
                        ? "Excellent — promises healthy progeny and longevity"
                        : "Nadi Dosha present — recommended to perform remedies (puja/donations)")
                .build();
    }

    // ==================== HELPERS ====================
    private int nakshatraIndex(String nakshatra) {
        if (nakshatra == null) return -1;
        for (int i = 0; i < NAKSHATRAS.length; i++) {
            if (NAKSHATRAS[i].equalsIgnoreCase(nakshatra)) return i;
        }
        return -1;
    }

    private int rashiIndex(String rashi) {
        if (rashi == null) return -1;
        for (int i = 0; i < RASHIS.length; i++) {
            if (RASHIS[i].equalsIgnoreCase(rashi)) return i;
        }
        return -1;
    }

    private GunaScore defaultScore(String name, int pts, int max, String result) {
        return GunaScore.builder().name(name).obtained(pts).maximum(max)
                .description("").result(result).build();
    }

    private String scoreText(int pts, int max, String good, String mid, String low) {
        double pct = (double) pts / max;
        if (pct >= 0.75) return good;
        if (pct >= 0.4) return mid;
        return low;
    }
}