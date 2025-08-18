package com.renewsim.backend.auth_service.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    private static final String CSP = "default-src 'none'; " +
            "base-uri 'none'; " +
            "frame-ancestors 'none'; " +
            "form-action 'none'; " +
            "block-all-mixed-content";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        response.setHeader("X-Content-Type-Options", "nosniff");

        response.setHeader("X-Frame-Options", "DENY");

        response.setHeader("Referrer-Policy", "no-referrer");

        response.setHeader("Content-Security-Policy", CSP);

        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", "max-age=15552000; includeSubDomains");
        }

        filterChain.doFilter(request, response);
    }
}

