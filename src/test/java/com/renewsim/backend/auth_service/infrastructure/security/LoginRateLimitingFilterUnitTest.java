package com.renewsim.backend.auth_service.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.auth_service.config.SecurityRateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimitingFilterUnitTest {

    @Test
    @DisplayName("No consume el body cuando strategy=IP_USER y el buffer está vacío (ContentCachingRequestWrapper)")
    void doesNotConsumeBody_whenStrategyIsIpUser_andCacheIsEmpty() throws ServletException, IOException {
        SecurityRateLimitProperties props = new SecurityRateLimitProperties();
        props.setEnabled(true);
        props.setStrategy(SecurityRateLimitProperties.Strategy.IP_USER);
        props.setMaxAttempts(2);
        props.setWindowSeconds(3);
        props.setRetryAfterSeconds(3);
        props.setLoginPath("/api/v1/auth/login");

        ObjectMapper objectMapper = new ObjectMapper();

        LoginRateLimitingFilter filter = new LoginRateLimitingFilter(props, objectMapper);

        String json = """
                {"username":"john.doe","email":"john.doe@example.com","password":"ValidPwd#2024"}
                """;
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("127.0.0.1");
        request.setContentType("application/json");
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> bodySeenDownstream = new AtomicReference<>();
        FilterChain chain = (req, res) -> {
            HttpServletRequest r = (HttpServletRequest) req;
            String seen = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            bodySeenDownstream.set(seen);
        };

        filter.doFilter(request, response, chain);

        assertEquals(json, bodySeenDownstream.get(), "El body debería estar intacto para el controller");
        assertEquals(200, response.getStatus() == 0 ? 200 : response.getStatus(),
                "En el primer intento no se debe bloquear (status 200 por defecto en MockHttpServletResponse)");
    }
}

