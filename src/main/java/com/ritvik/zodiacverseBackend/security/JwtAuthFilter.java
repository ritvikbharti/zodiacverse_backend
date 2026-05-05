package com.ritvik.zodiacverseBackend.security;

import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepo userRepo;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Authorization header for {} {}",
                    request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(token)) {
                log.warn("Invalid/expired JWT for {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Optional<User> userOpt = userRepo.findByEmail(email);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    email,  // ✅ principal = email string
                                    null,
                                    List.of(new SimpleGrantedAuthority(
                                            "ROLE_" + user.getRole().name()))
                            );

                    // ✅ DO NOT call WebAuthenticationDetailsSource here
                    // Set User entity directly as details
                    authToken.setDetails(user); // ✅ set AFTER constructor

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("✅ Authenticated user: {}", email);

                } else {
                    log.warn("User from token not found in DB: {}", email);
                }
            }

        } catch (Exception ex) {
            log.error("JWT processing error: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}