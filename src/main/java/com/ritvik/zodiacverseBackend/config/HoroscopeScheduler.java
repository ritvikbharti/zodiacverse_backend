package com.ritvik.zodiacverseBackend.config;

import com.ritvik.zodiacverseBackend.service.HoroscopeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class HoroscopeScheduler implements CommandLineRunner {

    private final HoroscopeGeneratorService generator;

    // Runs at midnight every day
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDaily() {
        log.info("⏰ Midnight cron: generating horoscopes...");
        generator.generateAllPeriodsForToday();
    }

    // Run once on startup too (so you have data immediately)
    @Override
    public void run(String... args) {
        log.info("🚀 Startup: generating today's horoscopes...");
        generator.generateAllPeriodsForToday();
    }
}