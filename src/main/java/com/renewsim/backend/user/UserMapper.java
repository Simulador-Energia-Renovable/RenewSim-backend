package com.renewsim.backend.user;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesFromStrings")
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "roles", expression = "java(mapRoles(entity.getRoles()))")
    UserResponseDTO toResponseDto(User entity);

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

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}