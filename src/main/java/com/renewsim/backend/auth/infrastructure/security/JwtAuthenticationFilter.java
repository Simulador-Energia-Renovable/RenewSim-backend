package com.renewsim.backend.auth.infrastructure.security;

import com.renewsim.backend.auth.application.port.out.TokenProvider;
import com.renewsim.backend.auth.domain.AuthenticatedUser;
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
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("Authentication already exists in SecurityContext. Skipping JWT filter.");
                return;
            }

            final String rawHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (rawHeader == null || rawHeader.isBlank()) {
                return;
            }
            final String header = rawHeader.trim();

            String token = extractBearerToken(header);
            if (token == null || token.isBlank()) {
                return;
            }
            token = token.trim();

            Optional<AuthenticatedUser> validatedUser = tokenProvider.validate(token);
            validatedUser.ifPresent(user -> setAuthentication(user, request));
            if (validatedUser.isEmpty()) {
                log.warn("JWT validation failed: token is invalid or expired");
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
        for (String r : Optional.ofNullable(user.roles()).orElse(Collections.emptySet())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
        }
        for (String s : Optional.ofNullable(user.scopes()).orElse(Collections.emptySet())) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
        }

        var authentication = new UsernamePasswordAuthenticationToken(user.username(), null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
