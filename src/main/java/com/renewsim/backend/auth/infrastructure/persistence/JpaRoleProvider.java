package com.renewsim.backend.auth.infrastructure.persistence;

import com.renewsim.backend.auth.application.port.out.RoleProvider;
import com.renewsim.backend.role.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaRoleProvider implements RoleProvider {
    @Override
    public RoleName defaultRole() {
        return RoleName.USER;
    }    
}
