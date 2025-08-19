package com.renewsim.backend.auth_service.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AuthorityMapper {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    private AuthorityMapper() { 

     }

    public static Collection<GrantedAuthority> mapToAuthorities(Set<String> roles, Set<String> scopes) {
        Stream<GrantedAuthority> roleAuths = safeStream(roles)
                .map(AuthorityMapper::normalizeRole)
                .filter(s -> !s.isBlank())
                .map(SimpleGrantedAuthority::new);

        Stream<GrantedAuthority> scopeAuths = safeStream(scopes)
                .map(AuthorityMapper::normalizeScope)
                .filter(s -> !s.isBlank())
                .map(SimpleGrantedAuthority::new);

        return Stream.concat(roleAuths, scopeAuths)
                .collect(Collectors.toUnmodifiableSet());
    }

    static String normalizeRole(String role) {
        if (role == null || role.isBlank()) return "";
        String trimmed = role.trim();
        return trimmed.startsWith(ROLE_PREFIX) ? trimmed : ROLE_PREFIX + trimmed;
    }

    static String normalizeScope(String scope) {
        if (scope == null || scope.isBlank()) return "";
        String trimmed = scope.trim();
        return trimmed.startsWith(SCOPE_PREFIX) ? trimmed : SCOPE_PREFIX + trimmed;
    }

    private static Stream<String> safeStream(Set<String> items) {
        return Objects.requireNonNullElse(items, Set.<String>of())
                .stream()
                .filter(Objects::nonNull);
    }
}
