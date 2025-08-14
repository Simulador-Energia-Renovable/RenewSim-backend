package com.renewsim.backend.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.auth.application.port.in.AuthUseCase;
import com.renewsim.backend.auth.infrastructure.security.JwtAuthenticationFilter;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

import java.time.Instant;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
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
    void testShouldAllowAuthEndpoints_AndSetNoStoreCacheOnLogin() throws Exception {
        String body = """
                  {"username":"john","password":"secret"}
                """;

        mvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(body))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-store")));
    }

     }