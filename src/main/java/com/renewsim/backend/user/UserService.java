package com.renewsim.backend.user;

import java.util.List;


public interface UserService {

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(Long id);

    UserResponseDTO save(UserRequestDTO dto);

    void deleteById(Long id);
}
