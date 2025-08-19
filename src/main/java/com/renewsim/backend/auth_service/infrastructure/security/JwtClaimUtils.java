package com.renewsim.backend.auth_service.infrastructure.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

final class JwtClaimUtils {
    private JwtClaimUtils() {}

    static Set<String> toStringSet(Object claim) {
        if (claim instanceof Collection<?> c) {
            return c.stream().map(Object::toString).collect(Collectors.toUnmodifiableSet());
        }
        return Set.of();
    }
}
