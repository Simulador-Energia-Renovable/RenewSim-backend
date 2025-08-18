package com.renewsim.backend.auth_service.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.renewsim.backend.auth_service.application.port.out.TokenProvider;
import com.renewsim.backend.auth_service.domain.AuthenticatedUser;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final Pattern BEARER_PATTERN = Pattern.compile("^Bearer\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/login")
            || path.startsWith("/auth/register")
            || path.startsWith("/actuator/health")
            || path.startsWith("/actuator/info");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("Authentication already present, skipping JWT validation.");
                return;
            }

            final String rawHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (rawHeader == null || rawHeader.isBlank()) {
                log.debug("No Authorization header present.");
                return;
            }

            String token = extractBearerToken(rawHeader.trim());
            if (token == null || token.isBlank()) {
                log.debug("Authorization header is not a Bearer token.");
                return;
            }

            Optional<AuthenticatedUser> validatedUser = tokenProvider.validate(token);
            if (validatedUser.isPresent()) {
                setAuthentication(validatedUser.get(), request);
            } else {
                log.warn("JWT validation failed: token is invalid, expired, or has incorrect claims.");
            }

        } catch (Exception e) {
            log.warn("JWT parsing/validation error: {}", e.getMessage());
        } finally {
            chain.doFilter(request, response);
        }
    }

    private String extractBearerToken(String header) {
        if (header.regionMatches(true, 0, BEARER, 0, BEARER.length())) {
            return header.substring(BEARER.length()).trim();
        }
        Matcher m = BEARER_PATTERN.matcher(header);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return null;
    }

    private void setAuthentication(AuthenticatedUser user, HttpServletRequest request) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Optional.ofNullable(user.roles()).orElse(Collections.emptySet())
                .forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));

        Optional.ofNullable(user.scopes()).orElse(Collections.emptySet())
                .forEach(s -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + s)));

        var authentication = new UsernamePasswordAuthenticationToken(user.username(), null, authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

