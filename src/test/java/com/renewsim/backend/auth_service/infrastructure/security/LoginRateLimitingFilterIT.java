package com.renewsim.backend.auth_service.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.auth_service.config.SecurityRateLimitProperties;
import com.renewsim.backend.auth_service.application.port.in.AuthUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "auth.rate-limiting.enabled=true",
        "auth.rate-limiting.strategy=IP_USER",
        "auth.rate-limiting.max-attempts=2",
        "auth.rate-limiting.window-seconds=3",
        "auth.rate-limiting.retry-after-seconds=3",
        "auth.rate-limiting.login-path=/api/v1/auth/login"
})
class LoginRateLimitingFilterIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SecurityRateLimitProperties props;

    @MockBean
    AuthUseCase authUseCase;

    private String jsonBody(String usernameOrEmail, String password) throws Exception {
        var dto = new java.util.HashMap<String, Object>();
        dto.put("username", usernameOrEmail);
        dto.put("email", usernameOrEmail);
        dto.put("password", password);
        return objectMapper.writeValueAsString(dto);
    }

    @Test
    @DisplayName("N+1 intentos dentro de ventana → 429; tras ventana → vuelve a 401/200 normal")
    void rateLimit_then_reset_window() throws Exception {
        when(authUseCase.login(any()))
                .thenThrow(new BadCredentialsException("invalid"));
        String body = jsonBody("john.doe", "InvalidPwd#123");

        var req = post("/api/v1/auth/login")
                .with(r -> {
                    r.setRemoteAddr("127.0.0.1");
                    return r;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);

        mockMvc.perform(req).andExpect(status().isUnauthorized());
        mockMvc.perform(req).andExpect(status().isUnauthorized());

        mockMvc.perform(req)
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"));

        Thread.sleep((props.getWindowSeconds() + 1L) * 1000);

        mockMvc.perform(req).andExpect(status().isUnauthorized());
    }

}