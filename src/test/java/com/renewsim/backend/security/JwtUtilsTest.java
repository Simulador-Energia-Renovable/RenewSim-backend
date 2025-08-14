package com.renewsim.backend.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.renewsim.backend.auth.config.security.JwtUtils;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "ZmFrZV9tZWdhdHJvX3NlY3JldF9mb3JfZGVtbw=="); 
        ReflectionTestUtils.setField(jwtUtils, "expirationSeconds", 3600L);
        ReflectionTestUtils.setField(jwtUtils, "clockSkewSeconds", 60L);
        ReflectionTestUtils.invokeMethod(jwtUtils, "init");
    }

    @Test
    void generateAndParseToken_ok() {
        String token = jwtUtils.generateToken("alice", Set.of("ADMIN"), Set.of("simulation:read"));
        Optional<Claims> claims = jwtUtils.validateAndGetClaims(token);
        assertThat(claims).isPresent();
        assertThat(jwtUtils.extractUsername(claims.get())).contains("alice");
        assertThat(jwtUtils.isExpired(claims.get())).isFalse();
    }
}

