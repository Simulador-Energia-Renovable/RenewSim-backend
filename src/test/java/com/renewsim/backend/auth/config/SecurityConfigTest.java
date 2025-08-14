package com.renewsim.backend.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.auth.application.port.in.AuthUseCase;
import com.renewsim.backend.auth.infrastructure.security.JwtAuthenticationFilter;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
                "cors.allowed-origins=http://localhost:3000"
})
@AutoConfigureMockMvc
class SecurityConfigTest {

        @Autowired
        private MockMvc mvc;

        @MockBean
        private AuthUseCase authUseCase;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        private ObjectMapper mapper;

        @BeforeEach
        void setUp() {
                mapper = new ObjectMapper();
                when(authUseCase.login(any())).thenReturn(
                                AuthResponseDTO.builder()
                                                .username("john")
                                                .token("mock-token")
                                                .tokenType("Bearer")
                                                .roles(Set.of("USER"))
                                                .scopes(Set.of("read"))
                                                .expiresAt(Instant.now().plusSeconds(3600))
                                                .build());
        }

        @Test
        @DisplayName("Auth endpoints should be public and set Cache-Control: no-store on login")
        void authEndpoints_public_and_noStore_on_login() throws Exception {
                String body = """
                                {"username":"john","password":"secret"}
                                """;

                mvc.perform(post("/api/v1/auth/login")
                                .contentType("application/json")
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-store")))
                                .andExpect(header().string(HttpHeaders.PRAGMA, containsString("no-cache")));
        }

        @Test
        @DisplayName("CORS preflight: should allow OPTIONS /** with allowed origin")
        void cors_preflight_options_any_path() throws Exception {
                mvc.perform(options("/api/v1/anything")
                                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                                .header("Access-Control-Request-Method", "POST"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                                .andExpect(header().string(HttpHeaders.VARY, containsString("Origin")))
                                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }

        @Test
        @DisplayName("Security headers should be present (CSP, HSTS, XFO, Referrer-Policy)")
        void security_headers_present_on_public_endpoint() throws Exception {
                mvc.perform(get("/error").secure(true))
                                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                                .andExpect(header().string("X-Frame-Options", "DENY"))
                                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
                                .andExpect(header().string("Content-Security-Policy", containsString("default-src")))
                                .andExpect(header().string("Strict-Transport-Security", containsString("max-age")));
        }

}
