package com.renewsim.backend.auth_service.infrastructure.policy;

import com.renewsim.backend.auth_service.application.port.out.ScopePolicy;
import com.renewsim.backend.role.RoleName;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class YamlScopePolicy implements ScopePolicy {

    private Map<String, List<String>> roleScopes = new HashMap<>();

    @Override
    public Set<String> scopesFor(RoleName roleName) {
        return new HashSet<>(roleScopes.getOrDefault(roleName.name(), List.of()));
    }
}
