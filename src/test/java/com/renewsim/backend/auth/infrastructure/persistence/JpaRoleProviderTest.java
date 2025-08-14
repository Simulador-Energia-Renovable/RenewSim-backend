
package com.renewsim.backend.auth.infrastructure.persistence;

import com.renewsim.backend.role.RoleName;
import org.junit.jupiter.api.Test;
import com.renewsim.backend.auth.application.port.out.RoleProvider;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class JpaRoleProviderTest {

    @Test
    @DisplayName("defaultRole() should always return RoleName.USER")
    void defaultRole_ShouldReturnUser() {
        RoleProvider roleProvider = new JpaRoleProvider();
        RoleName result = roleProvider.defaultRole();
        assertThat(result)
                .as("The default role must be USER")
                .isEqualTo(RoleName.USER);
    }
}