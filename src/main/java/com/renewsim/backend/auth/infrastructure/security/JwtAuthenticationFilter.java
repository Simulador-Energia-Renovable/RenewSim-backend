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
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("Authentication already exists in SecurityContext. Skipping JWT filter for this request.");
                return;
            }

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER)) {
                return;
            }

            final String token = authHeader.substring(BEARER.length()).trim();
            tokenProvider.validate(token).ifPresent(user -> authenticate(user, request));

        } catch (Exception ex) {
            log.debug("JWT filter error: {}", ex.getMessage(), ex);
        } finally {
            chain.doFilter(request, response);
        }
    }

    private void authenticate(AuthenticatedUser user, HttpServletRequest request) {
        var authorities = Stream.concat(
                user.roles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)),
                user.scopes() == null
                        ? Stream.<SimpleGrantedAuthority>empty()
                        : user.scopes().stream().map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
        ).toList();

        var authentication = new UsernamePasswordAuthenticationToken(
                user.username(),
                null,
                authorities
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

