package com.renewsim.backend.security;

import com.renewsim.backend.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthUtilsTest {

    @Test
    void getCurrentUser_ShouldThrowUnauthorizedException_WhenNoAuthentication() { 
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedException.class, AuthUtils::getCurrentUser);
    }
}

