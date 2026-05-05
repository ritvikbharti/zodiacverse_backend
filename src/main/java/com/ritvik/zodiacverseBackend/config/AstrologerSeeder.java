package com.ritvik.zodiacverseBackend.config;

import com.ritvik.zodiacverseBackend.model.Astrologer;
import com.ritvik.zodiacverseBackend.repo.AstrologerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AstrologerSeeder implements CommandLineRunner {

    private final AstrologerRepo astrologerRepo;

    @Override
    public void run(String... args) {
        if (astrologerRepo.count() > 0) return;   // already seeded

        List<Astrologer> seeds = List.of(
                build("Pandit Rajesh Sharma",
                        "Master of Vedic astrology with deep expertise in Kundli analysis and gemstone consultation.",
                        "Vedic,Marriage,Career", "Hindi,English",
                        25, "4.90", 1250, "15", "25", "40", true,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Rajesh"),

                build("Acharya Priya Verma",
                        "Specialist in love and relationship astrology with 15 years of experience.",
                        "Love,Relationships,Tarot", "Hindi,English,Marathi",
                        15, "4.80", 890, "12", "20", "35", true,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Priya"),

                build("Guru Vikram Singh",
                        "Expert in KP astrology and career guidance, helping thousands find their path.",
                        "KP,Career,Finance", "Hindi,English,Punjabi",
                        20, "4.70", 670, "18", "30", "50", false,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Vikram"),

                build("Dr. Meera Iyer",
                        "PhD in Vedic studies. Specializes in health astrology and remedies.",
                        "Vedic,Health,Remedies", "Tamil,English,Hindi",
                        18, "4.85", 1100, "20", "35", "55", true,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Meera"),

                build("Pandit Anil Kumar",
                        "Numerology and Vastu expert. Helping homes and businesses find harmony.",
                        "Numerology,Vastu,Lal Kitab", "Hindi,English",
                        22, "4.60", 540, "10", "18", "30", true,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Anil"),

                build("Ms. Sneha Kapoor",
                        "Western astrology and tarot reader. Modern approach to ancient wisdom.",
                        "Western,Tarot,Birth Chart", "English,Hindi",
                        10, "4.75", 420, "14", "22", "38", false,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Sneha"),

                build("Swami Devanand",
                        "Spiritual guide and Vedic astrologer. Focus on dharma and life purpose.",
                        "Vedic,Spirituality,Dasha", "Sanskrit,Hindi,English",
                        30, "4.95", 1850, "25", "45", "65", true,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Devanand"),

                build("Acharya Rohan Joshi",
                        "Young, energetic astrologer with modern approach. Specialty: career and education.",
                        "Vedic,Career,Education", "Hindi,English,Marathi",
                        8, "4.65", 310, "10", "16", "28", true,
                        "https://api.dicebear.com/7.x/avataaars/svg?seed=Rohan")
        );

        astrologerRepo.saveAll(seeds);
        System.out.println("Seeded " + seeds.size() + " astrologers");
    }

    private Astrologer build(String name, String bio, String specialties, String langs,
                             int exp, String rating, int reviews,
                             String chat, String voice, String video, boolean online,
                             String avatar) {
        return Astrologer.builder()
                .name(name).bio(bio)
                .specialties(specialties).languages(langs)
                .experienceYears(exp)
                .rating(new BigDecimal(rating))
                .reviewsCount(reviews)
                .chatRate(new BigDecimal(chat))
                .voiceRate(new BigDecimal(voice))
                .videoRate(new BigDecimal(video))
                .online(online).avatarUrl(avatar).build();
    }
}