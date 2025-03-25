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

    @Mapping(target = "id", ignore = true) // IGNORAR ID al crear
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesFromStrings")
    User toEntity(UserRequestDTO dto);
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

    UserResponseDTO toResponseDto(User entity);

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());
    }
}
