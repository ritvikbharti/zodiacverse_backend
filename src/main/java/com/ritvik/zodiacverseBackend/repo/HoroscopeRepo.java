package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Horoscope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoroscopeRepo extends JpaRepository<Horoscope, UUID> {

    List<Horoscope> findByPeriodAndHoroscopeDateOrderBySign(String period, LocalDate date);

    Optional<Horoscope> findBySignAndPeriodAndHoroscopeDate(String sign, String period, LocalDate date);
}