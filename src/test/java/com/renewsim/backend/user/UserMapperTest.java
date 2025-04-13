package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.user.dto.UserRequestDTO;
import com.renewsim.backend.user.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper Test")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {      
        userMapper = new UserMapperImpl();
    }

    @Test
    @DisplayName("Should map UserRequestDTO to User entity")
    void shouldMapUserRequestDTOToUserEntity() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .username("testuser")
                .password("securepassword")
                .roles(Set.of("ADMIN", "USER"))
                .build();

        User user = userMapper.toEntity(dto);

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(dto.getUsername());
        assertThat(user.getPassword()).isEqualTo(dto.getPassword());
        assertThat(user.getRoles()).extracting(Role::getName)
                .containsExactlyInAnyOrder(RoleName.ADMIN, RoleName.USER);
    }

    @Test
    @DisplayName("Should map User entity to UserResponseDTO")
    void shouldMapUserEntityToUserResponseDTO() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(Set.of(role));

        UserResponseDTO responseDTO = userMapper.toResponseDto(user);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(user.getId());
        assertThat(responseDTO.getUsername()).isEqualTo(user.getUsername());
        assertThat(responseDTO.getRoles()).containsExactly("ADMIN");
    }

    @Test
    @DisplayName("Should map roles to role names")
    void shouldMapRolesToRoleNames() {
        Role role = new Role();
        role.setName(RoleName.USER);

        Set<String> roleNames = userMapper.mapRoleNames(Set.of(role));

        assertThat(roleNames).containsExactly("USER");
    }

    @Test
    @DisplayName("Should map role names to roles")
    void shouldMapRoleNamesToRoles() {
        Set<Role> roles = userMapper.mapRolesFromStrings(Set.of("ADMIN"));

        assertThat(roles).hasSize(1);
        Role role = roles.iterator().next();
        assertThat(role.getName()).isEqualTo(RoleName.ADMIN);
    }
}

