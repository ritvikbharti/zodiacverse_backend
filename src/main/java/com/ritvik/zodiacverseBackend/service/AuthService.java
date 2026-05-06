package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.AuthResponse;
import com.ritvik.zodiacverseBackend.dto.LoginRequest;
import com.ritvik.zodiacverseBackend.dto.RegisterRequest;
import com.ritvik.zodiacverseBackend.model.RefreshToken;
import com.ritvik.zodiacverseBackend.model.Role;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.RefreshTokenRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import com.ritvik.zodiacverseBackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.ritvik.zodiacverseBackend.service.EmailService;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepository;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService; //INJECT
    private final NotificationService notificationService; // ✅ ADD THIS



    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already registered");
        }

        User user = User.builder()
                .fullname(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullname());
        notificationService.notifyWelcome(user);                            // ✅ ADD THIS LINE

        return generateTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        notificationService.notifyWelcome(user);                            // ✅ ADD THIS LINE
//        emailService.sendWelcomeEmail(user.getEmail(), user.getFullname());

        return generateTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {

        RefreshToken stored = refreshTokenRepo.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(stored);
            throw new RuntimeException("Refresh token expired - please login again");
        }

        if (!jwtService.isTokenValid(refreshTokenStr)) {
            throw new RuntimeException("Invalid refresh token signature");
        }

        User user = stored.getUser();

        // Rotate: delete old token, issue new pair (more secure)
        refreshTokenRepo.delete(stored);

        return generateTokens(user);
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepo.findByToken(refreshTokenStr)
                .ifPresent(refreshTokenRepo::delete);
    }

    // ---------- helper ----------
    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshTokenStr = jwtService.generateRefreshToken(user.getEmail());

        LocalDateTime expiresAt = Instant
                .now()
                .plusMillis(refreshExpirationMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        refreshTokenRepo.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }
}