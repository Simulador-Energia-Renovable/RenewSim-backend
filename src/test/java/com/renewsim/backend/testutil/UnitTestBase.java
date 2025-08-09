package com.renewsim.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class UnitTestBase {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void baseSetUp() {
        // Put here common in-memory fixtures if needed
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void baseTearDown() {
        SecurityContextHolder.clearContext();
    }

    protected String toJson(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
