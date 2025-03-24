package com.renewsim.backend.service;

import java.util.List;

import com.renewsim.backend.dto.UserResponseDTO;
import com.renewsim.backend.dto.UserRequestDTO;

public interface UserService {

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(Long id);

    UserResponseDTO save(UserRequestDTO dto);

    void deleteById(Long id);
}
