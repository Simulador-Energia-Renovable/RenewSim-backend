package com.renewsim.backend.security;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserDetailsImpl Tests")
class UserDetailsImplTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password";

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        Role roleUser = new Role();
        roleUser.setName(RoleName.USER);

        Role roleAdmin = new Role();
        roleAdmin.setName(RoleName.ADMIN);

        user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setRoles(Set.of(roleUser, roleAdmin));

        userDetails = new UserDetailsImpl(user);
    }

    @Test
    @DisplayName("should return correct authorities")
    void shouldReturnCorrectAuthorities() {
        var authorities = userDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_USER")));
        assertTrue(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("should return correct username")
    void shouldReturnCorrectUsername() {
        assertEquals(TEST_USERNAME, userDetails.getUsername());
    }

    @Test
    @DisplayName("should return correct password")
    void shouldReturnCorrectPassword() {
        assertEquals(TEST_PASSWORD, userDetails.getPassword());
    }

    @Test
    @DisplayName("should return true for account status checks")
    void shouldReturnTrueForAccountStatus() {
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("should return associated user")
    void shouldReturnAssociatedUser() {
        assertEquals(user, userDetails.getUser());
    }
}
