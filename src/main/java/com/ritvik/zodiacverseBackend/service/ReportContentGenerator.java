package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.model.Kundli;
import com.ritvik.zodiacverseBackend.model.ReportType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ReportContentGenerator {

    public String generate(Kundli k, ReportType type) {
        return switch (type) {
            case CAREER -> career(k);
            case LOVE -> love(k);
            case HEALTH -> health(k);
            case FINANCE -> finance(k);
            case COMPLETE -> complete(k);
        };
    }

    private String career(Kundli k) {
        return sections(k, "Career & Professional Life",
                section("Career Overview",
                        "With your Sun in " + k.getSunSign() + " and Ascendant in " + k.getAscendant() +
                                ", you possess a natural drive for leadership and excellence. " +
                                "Your 10th house, the house of career, shows tremendous potential for success in fields " +
                                "related to communication, creativity, and management."),
                section("Ideal Professions",
                        "Based on your " + k.getSunSign() + " sun and " + k.getMoonSign() + " moon placement, " +
                                "you are best suited for careers in: Technology & Innovation, Management & Leadership, " +
                                "Creative Arts, Consultancy, or Entrepreneurship. " +
                                "Your Nakshatra " + k.getNakshatra() + " indicates a sharp analytical mind."),
                section("Career Timeline",
                        "• 2025-2026: Period of learning and consolidation. Focus on skill development.\n" +
                                "• 2026-2028: Strong period for promotions and recognition.\n" +
                                "• 2028-2030: Excellent time for entrepreneurship or major career changes.\n" +
                                "• 2030+: Peak professional phase — leadership roles beckon."),
                section("Strengths at Work",
                        "Your " + k.getMoonSign() + " moon gives you exceptional emotional intelligence. " +
                                "You work best in collaborative environments and excel at bringing teams together. " +
                                "Natural problem-solving abilities and creative thinking set you apart."),
                section("Career Challenges",
                        "Guard against overthinking and indecisiveness — typical traits when " +
                                k.getAscendant() + " is prominent. Learn to take calculated risks. " +
                                "Mercury placement suggests communication should be chosen carefully in professional settings."),
                section("Remedies for Career Growth",
                        "• Wear a Blue Sapphire (Neelam) on Saturday for Saturn's blessings\n" +
                                "• Recite Gayatri Mantra 108 times every morning\n" +
                                "• Light a lamp at home temple every Thursday\n" +
                                "• Donate to educational institutions on Wednesdays")
        );
    }

    private String love(Kundli k) {
        return sections(k, "Love & Relationships",
                section("Love Personality",
                        "With Moon in " + k.getMoonSign() + ", your emotional world is rich and deep. " +
                                "You seek genuine connection and intellectual stimulation in relationships. " +
                                "Your " + k.getSunSign() + " nature makes you magnetic and attractive to potential partners."),
                section("Ideal Partner Qualities",
                        "Your ideal partner would have strong Earth or Water sign placements to balance your energy. " +
                                "Look for someone who values emotional depth, loyalty, and growth. " +
                                "Compatibility is highest with those born under Taurus, Cancer, Scorpio, or Pisces sun signs."),
                section("Love Life Timeline",
                        "• 2025: A period of introspection and self-love — important foundations.\n" +
                                "• 2026: Strong romantic possibilities — Venus transit favors love.\n" +
                                "• 2027: Deep commitments and strengthening of existing relationships.\n" +
                                "• 2028-2029: Marriage/long-term commitment is highly favored."),
                section("Relationship Strengths",
                        "You are deeply loyal, caring, and intuitive about your partner's needs. " +
                                "Your " + k.getMoonSign() + " moon placement makes you an exceptionally nurturing partner. " +
                                "Communication flows naturally and you value honesty above all."),
                section("Relationship Challenges",
                        "Tendency towards jealousy or possessiveness may arise — especially during retrograde phases. " +
                                "Learn to give space and trust. Guard against emotional over-investment early in relationships."),
                section("Love Remedies",
                        "• Wear Rose Quartz or Pearl for Venus's blessings\n" +
                                "• Offer red flowers to Goddess Lakshmi on Fridays\n" +
                                "• Chant 'Om Shukraya Namaha' 108 times on Fridays\n" +
                                "• Light rose incense on full moon nights")
        );
    }

    private String health(Kundli k) {
        return sections(k, "Health & Wellness",
                section("Health Overview",
                        "Your Lagna (Ascendant) " + k.getAscendant() + " rules your physical constitution. " +
                                "Overall vitality is indicated as good, with natural resilience. " +
                                "The 6th house analysis suggests being mindful of stress-related issues."),
                section("Body Constitution (Prakriti)",
                        "Based on your birth chart, your dominant constitution is Vata-Pitta. " +
                                "You tend to be energetic, quick-thinking, but may face digestion and nervous system issues " +
                                "when out of balance. Regular routine and warm, nourishing foods are recommended."),
                section("Health Sensitive Periods",
                        "• Be cautious during Saturn transit over 1st/6th/8th houses\n" +
                                "• Rahu-Ketu axis may cause unusual health patterns 2025-2026\n" +
                                "• Mercury retrograde periods: avoid major medical decisions\n" +
                                "• Strongest health period: Jupiter's transit through benefic houses"),
                section("Areas to Watch",
                        "Your " + k.getSunSign() + " placement suggests being mindful of: " +
                                "Stress management, immune system support, adequate sleep, and digestive health. " +
                                "Regular exercise aligned with your Pitta nature (swimming, yoga, moderate cardio) is beneficial."),
                section("Wellness Practices",
                        "• Morning sun salutation (Surya Namaskar) aligned with your sun in " + k.getSunSign() + "\n" +
                                "• Meditation for mental clarity — your Moon in " + k.getMoonSign() + " craves stillness\n" +
                                "• Avoid fasting on days ruled by your weak planets\n" +
                                "• Drink copper-vessel water every morning"),
                section("Health Remedies",
                        "• Mahamrityunjaya Mantra for overall health protection\n" +
                                "• Wear Rudraksha mala for nervous system balance\n" +
                                "• Donate medicines on Saturdays\n" +
                                "• Regular Ayurvedic oil massage (Abhyanga) on Sundays")
        );
    }

    private String finance(Kundli k) {
        return sections(k, "Finance & Wealth",
                section("Financial Overview",
                        "With your 2nd house (house of wealth) and 11th house (house of gains) analysis, " +
                                "your chart indicates a progressive financial journey. " +
                                "Sun in " + k.getSunSign() + " suggests self-made wealth through hard work and intellect."),
                section("Income Sources",
                        "Primary income sources favored by your chart: professional services, technology, " +
                                "creative fields, or business. Secondary income through: investments, partnerships, " +
                                "or side ventures in your area of expertise will be particularly lucrative."),
                section("Financial Timeline",
                        "• 2025: Steady income, focus on savings and debt reduction\n" +
                                "• 2026: Investment opportunities arise — trust but verify\n" +
                                "• 2027: Significant income boost through career advancement\n" +
                                "• 2028-2030: Wealth accumulation phase — best years for major investments"),
                section("Investment Guidance",
                        "Your " + k.getMoonSign() + " Moon indicates intuitive investment instincts. " +
                                "Favorable sectors: Technology, Real Estate, Healthcare, and Gold. " +
                                "Avoid speculative investments during Rahu-Ketu transit periods. " +
                                "SIP investments started on Thursday will yield strong returns."),
                section("Financial Challenges",
                        "Guard against impulsive spending — especially during Venus retrograde. " +
                                "Avoid lending money to friends during difficult planetary periods. " +
                                "Property disputes should be resolved through mediation, not litigation."),
                section("Wealth Remedies",
                        "• Keep a Shree Yantra at your workplace\n" +
                                "• Donate food to the needy on Thursdays (Jupiter's day)\n" +
                                "• Chant 'Om Shreem Hreem Kleem Mahalakshmiyei Namaha' daily\n" +
                                "• Never let your wallet stay empty — keep a yellow folded cloth inside")
        );
    }

    private String complete(Kundli k) {
        return sections(k, "Complete Life Analysis",
                section("Soul Purpose & Life Path",
                        "Your Sun in " + k.getSunSign() + " with Moon in " + k.getMoonSign() +
                                " and Ascendant " + k.getAscendant() + " creates a unique cosmic signature. " +
                                "Your Nakshatra " + k.getNakshatra() + " (Rashi: " + k.getRashi() + ") points to a soul " +
                                "that has come to experience growth through creativity, service, and deep connections."),
                section("Personality & Character",
                        "You blend the " + k.getSunSign() + " energy's outward expression with the " +
                                k.getMoonSign() + " moon's inner emotional world. " +
                                "People see you through the lens of your " + k.getAscendant() + " Ascendant — " +
                                "appearing confident, thoughtful, and magnetic."),
                section("Career & Finances",
                        "Professional success comes through leveraging your natural leadership and intellectual gifts. " +
                                "Peak financial years are indicated between ages 35-45. " +
                                "Real estate and knowledge-based businesses are highly favored."),
                section("Love & Relationships",
                        "Deep, meaningful connections are your hallmark. You give wholly in love. " +
                                "Marriage is indicated after thorough mental and emotional compatibility is established. " +
                                "Best compatible signs: those complementing your elemental nature."),
                section("Health & Longevity",
                        "Naturally resilient constitution with good recovery abilities. " +
                                "Mind-body practices will dramatically enhance your quality of life. " +
                                "Be particularly mindful of health during ages 28-30 (Saturn return) and 42-44."),
                section("Spiritual Growth",
                        "Your Nakshatra " + k.getNakshatra() + " has deep connections to spiritual wisdom. " +
                                "Meditation, yoga, and service to others form your spiritual path. " +
                                "Pilgrimage to a water-based sacred site will bring profound inner peace."),
                section("Lucky Factors",
                        "• Lucky Numbers: 3, 7, 9\n" +
                                "• Lucky Colors: Based on your " + k.getSunSign() + " — Gold, White, Purple\n" +
                                "• Lucky Days: Monday and Thursday\n" +
                                "• Lucky Gemstone: Based on your Lagna — consult for personalized recommendation\n" +
                                "• Lucky Direction: North-East for sleeping, East for working"),
                section("Overall Prediction",
                        "The cosmic blueprint of your birth chart reveals a soul of tremendous potential. " +
                                "With conscious effort, spiritual practice, and aligned action, " +
                                "the coming years hold extraordinary promise for growth, love, and fulfillment. " +
                                "The stars are aligned in your favor — it is time to rise and shine. ✨")
        );
    }

    // ---- HTML template helpers ----
    private String sections(Kundli k, String reportTitle, String... sectionHtmls) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8"/>
            <style>
              body { font-family: 'Helvetica Neue', Arial, sans-serif; color: #2d2d2d; margin: 0; padding: 0; }
              .cover { background: linear-gradient(135deg, #1a0533 0%, #2d0a5e 50%, #1a0533 100%);
                       color: white; padding: 60px 40px; text-align: center; min-height: 200px; }
              .cover h1 { font-size: 32px; margin: 0 0 8px 0; letter-spacing: 1px; }
              .cover p  { font-size: 14px; opacity: 0.8; margin: 4px 0; }
              .badge    { display: inline-block; background: rgba(255,255,255,0.2);
                          border-radius: 20px; padding: 4px 16px; font-size: 12px; margin-top: 12px; }
              .body     { padding: 32px 40px; }
              .section  { margin-bottom: 28px; border-left: 4px solid #7c3aed; padding-left: 16px; }
              .section h2 { font-size: 18px; color: #7c3aed; margin: 0 0 10px 0; }
              .section p  { font-size: 13px; line-height: 1.8; color: #444; margin: 0; white-space: pre-line; }
              .divider  { border: none; border-top: 1px solid #e5e5e5; margin: 20px 0; }
              .footer   { text-align: center; padding: 20px; font-size: 11px; color: #999;
                          border-top: 1px solid #eee; margin-top: 20px; }
              .meta-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin: 20px 0; }
              .meta-box  { background: #f8f5ff; border-radius: 8px; padding: 12px; text-align: center; }
              .meta-box .label { font-size: 10px; color: #888; text-transform: uppercase; }
              .meta-box .value { font-size: 14px; font-weight: bold; color: #7c3aed; margin-top: 4px; }
            </style>
            </head>
            <body>
            <div class="cover">
              <p class="badge">ZodiacVerse · Astrological Report</p>
              <h1>""").append(reportTitle).append("</h1>")
                .append("<p>Prepared for: <strong>").append(k.getPersonName()).append("</strong></p>")
                .append("<p>Born: ").append(k.getBirthDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                .append(" · ").append(k.getBirthTime()).append(" · ").append(k.getBirthPlace()).append("</p>")
                .append("<p>Generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))).append("</p>")
                .append("</div>")
                .append("<div class=\"body\">")
                .append("<div class=\"meta-grid\">")
                .append(metaBox("Sun Sign", k.getSunSign()))
                .append(metaBox("Moon Sign", k.getMoonSign()))
                .append(metaBox("Ascendant", k.getAscendant()))
                .append(metaBox("Nakshatra", k.getNakshatra()))
                .append(metaBox("Rashi", k.getRashi()))
                .append(metaBox("System", "Vedic"))
                .append("</div><hr class=\"divider\"/>");

        for (String sec : sectionHtmls) {
            sb.append(sec);
        }

        sb.append("""
            </div>
            <div class="footer">
              © ZodiacVerse · This report is for guidance purposes only ·
              For detailed consultation, speak with our expert astrologers.
            </div>
            </body></html>
            """);

        return sb.toString();
    }

    private String section(String title, String content) {
        return "<div class=\"section\"><h2>" + title + "</h2><p>" + content + "</p></div>";
    }

    private String metaBox(String label, String value) {
        return "<div class=\"meta-box\"><div class=\"label\">" + label +
                "</div><div class=\"value\">" + (value != null ? value : "N/A") + "</div></div>";
    }
}