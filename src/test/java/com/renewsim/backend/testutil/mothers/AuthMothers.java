package com.renewsim.backend.testutil.mothers;

import java.util.Set;

import com.renewsim.backend.auth_service.domain.AuthenticatedUser;
import com.renewsim.backend.auth_service.web.dto.AuthRequestDTO;

public final class AuthMothers {
    private AuthMothers() {
    }

    public static AuthenticatedUser userJohn() {
        return new AuthenticatedUser("john", Set.of("USER"), Set.of("read"));
    }

    public static AuthRequestDTO loginJohn() {
        return new AuthRequestDTO("john", "secret");
    }
}
