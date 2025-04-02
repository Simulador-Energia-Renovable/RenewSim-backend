package com.renewsim.backend.user;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName; // Ensure RoleName is imported


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoleNames")
    UserResponseDTO toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesFromStrings")
    User toEntity(UserRequestDTO dto);

    @Named("mapRoleNames")
    default Set<String> mapRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    @Named("mapRolesFromStrings")
    default Set<Role> mapRolesFromStrings(Set<String> roles) {
        return roles.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(RoleName.valueOf(roleName));
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
