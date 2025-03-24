package com.renewsim.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.renewsim.backend.dto.UserRequestDTO;
import com.renewsim.backend.dto.UserResponseDTO;
import com.renewsim.backend.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true) // IGNORAR ID al crear
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDto(User entity);
}
