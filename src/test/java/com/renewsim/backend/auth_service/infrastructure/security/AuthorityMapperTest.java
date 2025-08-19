package com.renewsim.backend.auth_service.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityMapperTest {

    @Test
    @DisplayName("Roles should be normalized to ROLE_*")
    void rolesAreNormalized() {
        Collection<GrantedAuthority> auths = AuthorityMapper.mapToAuthorities(Set.of("ADMIN"), Set.of());
        assertThat(auths).extracting(GrantedAuthority::getAuthority).contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Scopes should be normalized to SCOPE_*")
    void scopesAreNormalized() {
        Collection<GrantedAuthority> auths = AuthorityMapper.mapToAuthorities(Set.of(), Set.of("read:simulations"));
        assertThat(auths).extracting(GrantedAuthority::getAuthority).contains("SCOPE_read:simulations");
    }

    @Test
    @DisplayName("Do not duplicate prefixes if already present")
    void avoidDuplicatePrefixes() {
        Collection<GrantedAuthority> auths = AuthorityMapper.mapToAuthorities(Set.of("ROLE_ADMIN"), Set.of("SCOPE_read:simulations"));
        assertThat(auths).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "SCOPE_read:simulations");
    }
    
}

