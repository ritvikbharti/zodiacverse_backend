package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.model.Horoscope;
import com.ritvik.zodiacverseBackend.repo.HoroscopeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class HoroscopeGeneratorService {

    private final HoroscopeRepo horoscopeRepo;

    private static final String[] SIGNS = {
            "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
            "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };

    private static final String[] EMOJIS = {
            "♈", "♉", "♊", "♋", "♌", "♍",
            "♎", "♏", "♐", "♑", "♒", "♓"
    };

    private static final String[] MOODS = {
            "Energetic", "Romantic", "Focused", "Creative", "Peaceful",
            "Ambitious", "Reflective", "Joyful", "Determined", "Intuitive",
            "Confident", "Inspired"
    };

    private static final String[] COLORS = {
            "Red", "Green", "Yellow", "Blue", "Gold", "White",
            "Purple", "Orange", "Pink", "Silver", "Violet", "Turquoise"
    };

    //  Different templates per period
    private static final String[][] DAILY_TEMPLATES = {
            {
                    "Today the cosmos sends a powerful wave of {mood_lower} energy your way, {sign}. " +
                            "Trust your instincts and act on the opportunities that present themselves. " +
                            "A meaningful conversation could shift your perspective significantly.",
                    "Lucky color {color} enhances your aura. The number {lucky} holds special power today."
            },
            {
                    "Mercury aligns in your favor today, {sign}, sharpening your mind and communication. " +
                            "A {mood_lower} spirit guides every interaction you have. " +
                            "Don't overthink — the right answer comes naturally.",
                    "Someone from your past may reappear with important news. Stay open and grounded."
            },
            {
                    "The moon illuminates your path today, dear {sign}. " +
                            "Your {mood_lower} energy draws positive people and situations toward you. " +
                            "This is an excellent day for starting new projects or conversations.",
                    "Wear {color} today to amplify cosmic vibrations. Lucky number {lucky} brings fortune."
            },
    };

    private static final String[][] WEEKLY_TEMPLATES = {
            {
                    "This week brings a powerful shift in your energy, {sign}. " +
                            "The planetary alignment favors bold decisions and new beginnings. " +
                            "Your {mood_lower} nature will be your greatest strength Monday through Wednesday.",
                    "By Thursday, financial matters come into focus — review your budget carefully. " +
                            "The weekend brings joy through family or creative pursuits. Lucky color: {color}."
            },
            {
                    "A week of transformation awaits you, {sign}. " +
                            "Early in the week, focus on completing unfinished tasks. " +
                            "Midweek brings unexpected opportunities — stay alert and {mood_lower}.",
                    "The weekend is ideal for rest and reflection. " +
                            "Lucky number {lucky} guides important decisions this week. " +
                            "Avoid major financial commitments on Friday."
            },
            {
                    "Venus graces your week with warmth and connection, {sign}. " +
                            "Relationships — both personal and professional — deepen meaningfully. " +
                            "Your {mood_lower} energy peaks on Wednesday and Thursday.",
                    "Use the weekend to recharge. Meditation or time in nature restores your spirit. " +
                            "Color {color} brings clarity during challenging moments this week."
            },
    };

    private static final String[][] MONTHLY_TEMPLATES = {
            {
                    "This month marks a significant turning point for you, {sign}. " +
                            "Jupiter's influence in the first two weeks brings expansion in career and finances. " +
                            "Your {mood_lower} energy is at an all-time high — use it wisely.",
                    "The third week may bring emotional challenges — lean on trusted loved ones. " +
                            "Month's end is ideal for new commitments and long-term planning. " +
                            "Lucky color {color} and number {lucky} guide your month."
            },
            {
                    "A month of deep growth and self-discovery unfolds for {sign}. " +
                            "The new moon early this month seeds powerful intentions — plant them carefully. " +
                            "Career matters accelerate mid-month with {mood_lower} energy fueling your ambitions.",
                    "The full moon reveals hidden truths in relationships — embrace honesty. " +
                            "Financial investments made this month carry 3-year positive returns. " +
                            "Wear {color} during important meetings for best results."
            },
            {
                    "Saturn's steady hand shapes this productive month for {sign}. " +
                            "Discipline and consistency bring remarkable rewards over the coming weeks. " +
                            "Your {mood_lower} nature helps you stay the course when others waver.",
                    "Health and wellness deserve attention mid-month — start a new routine. " +
                            "Love life flourishes in the final week. Lucky number {lucky} appears " +
                            "at key decision points — trust the signs."
            },
    };

    private static final String[][] YEARLY_TEMPLATES = {
            {
                    "This year heralds a profound chapter of growth and abundance for {sign}. " +
                            "Jupiter's year-long transit through your chart brings expansion across all life areas. " +
                            "Your {mood_lower} spirit will attract remarkable opportunities from unexpected sources.",
                    "Career peaks between April and August — position yourself for leadership. " +
                            "Relationships deepen in ways that reshape your understanding of love. " +
                            "Lucky color {color} and number {lucky} are your cosmic companions this year."
            },
            {
                    "The year ahead is one of transformation and empowerment, dear {sign}. " +
                            "Saturn's discipline combines with Rahu's innovation to create an unstoppable force. " +
                            "Channel your {mood_lower} energy into long-term goals — the harvest is coming.",
                    "Financial growth accelerates in the second half of the year. " +
                            "A pivotal relationship either deepens into lifelong commitment or gracefully concludes. " +
                            "Either way, you emerge stronger, wiser, and more aligned with your true self."
            },
            {
                    "Rare planetary alignments make this a landmark year for {sign}. " +
                            "The first quarter demands patience and preparation. " +
                            "Trust the process — your {mood_lower} nature is building something magnificent.",
                    "Mid-year brings breakthroughs that seemed impossible earlier. " +
                            "Travel, higher education, or spiritual exploration transforms your perspective. " +
                            "Year-end celebrations are well-deserved. Color {color} marks your victorious moments."
            },
    };

    // Key fix: each period gets a DIFFERENT reference date
    public LocalDate getDateForPeriod(String period) {
        LocalDate today = LocalDate.now();
        return switch (period.toLowerCase()) {
            case "daily"   -> today;
            case "weekly"  -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "monthly" -> today.with(TemporalAdjusters.firstDayOfMonth());
            case "yearly"  -> today.with(TemporalAdjusters.firstDayOfYear());
            default        -> today;
        };
    }

    public void generateForPeriod(String period) {
        LocalDate periodDate = getDateForPeriod(period);

        // Choose template set based on period
        String[][] templates = switch (period.toLowerCase()) {
            case "weekly"  -> WEEKLY_TEMPLATES;
            case "monthly" -> MONTHLY_TEMPLATES;
            case "yearly"  -> YEARLY_TEMPLATES;
            default        -> DAILY_TEMPLATES;
        };

        // Use periodDate as seed so same period always gives same results
        Random rand = new Random(periodDate.toEpochDay() * 31L + period.hashCode());

        for (int i = 0; i < SIGNS.length; i++) {
            String sign = SIGNS[i];

            // Skip if already generated for this period+date
            if (horoscopeRepo.findBySignAndPeriodAndHoroscopeDate(sign, period, periodDate).isPresent()) {
                log.info("⏭ Skipping {}/{} — already exists", sign, period);
                continue;
            }

            String[] template = templates[rand.nextInt(templates.length)];
            String mood  = MOODS[i % MOODS.length];
            int lucky    = (i % 9) + 1;
            String color = COLORS[i % COLORS.length];

            String text = (template[0] + " " + template[1])
                    .replace("{sign}", sign)
                    .replace("{mood_lower}", mood.toLowerCase())
                    .replace("{color}", color)
                    .replace("{lucky}", String.valueOf(lucky));

            horoscopeRepo.save(Horoscope.builder()
                    .sign(sign).period(period)
                    .horoscopeDate(periodDate)    //  different date per period
                    .text(text).mood(mood)
                    .luckyNumber(lucky).color(color)
                    .emoji(EMOJIS[i])
                    .build());
        }
        log.info(" {} horoscopes generated for {} ({})", SIGNS.length, period, periodDate);
    }

    public void generateAllPeriodsForToday() {
        List.of("daily", "weekly", "monthly", "yearly")
                .forEach(this::generateForPeriod);
    }
}