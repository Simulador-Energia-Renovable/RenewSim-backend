package com.renewsim.backend.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true) // IGNORAR ID al crear
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDto(User entity);
}
