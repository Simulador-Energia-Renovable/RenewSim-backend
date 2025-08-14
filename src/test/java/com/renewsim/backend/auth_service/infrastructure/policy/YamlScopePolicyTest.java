package com.renewsim.backend.auth_service.infrastructure.policy;
import com.renewsim.backend.role.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class YamlScopePolicyTest {

    @Test
    @DisplayName("scopesFor(USER) should return configured scopes")
    void testShouldReturnConfiguredScopes_ForKnownRole() {
        YamlScopePolicy policy = new YamlScopePolicy();
        Map<String, List<String>> map = new HashMap<>();
        map.put("USER", List.of("simulation:read", "profile:read"));
        map.put("ADMIN", List.of("admin:write"));
        policy.setRoleScopes(map);

        Set<String> scopes = policy.scopesFor(RoleName.USER);

        assertThat(scopes)
                .containsExactlyInAnyOrder("simulation:read", "profile:read");
    }

    @Test
    @DisplayName("scopesFor(unknown) should return empty set")
    void testShouldReturnEmptySet_ForUnknownRole() {
       YamlScopePolicy policy = new YamlScopePolicy();
        policy.setRoleScopes(Map.of("USER", List.of("simulation:read")));

        Set<String> scopes = policy.scopesFor(RoleName.ADMIN);

        assertThat(scopes).isEmpty();
    }

    @Test
    @DisplayName("scopesFor returns a defensive copy (internal map must not be mutated)")
    void testShouldReturnDefensiveCopy() {
        YamlScopePolicy policy = new YamlScopePolicy();
        policy.setRoleScopes(new HashMap<>(Map.of(
                "USER", new ArrayList<>(List.of("simulation:read"))
        )));

        Set<String> first = policy.scopesFor(RoleName.USER);
        first.add("hacked:scope"); 

        Set<String> second = policy.scopesFor(RoleName.USER);
        assertThat(second).containsExactly("simulation:read");
    }
}
