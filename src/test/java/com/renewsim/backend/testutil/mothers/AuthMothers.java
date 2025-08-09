package com.renewsim.backend.testutil.mothers;

import com.renewsim.backend.auth.domain.AuthenticatedUser;
import com.renewsim.backend.auth.web.dto.AuthRequestDTO;

import java.util.Set;

public final class AuthMothers {
    private AuthMothers(){}

    public static AuthenticatedUser userJohn() {
        return new AuthenticatedUser("john", Set.of("ROLE_USER"), Set.of("read"));
    }

    public static AuthRequestDTO loginJohn() {
        return new AuthRequestDTO("john", "secret");
    }
}

