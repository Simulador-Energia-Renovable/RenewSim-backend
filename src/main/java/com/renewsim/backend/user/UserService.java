package com.renewsim.backend.user;

import java.util.List;

import com.renewsim.backend.role.RoleDTO;


public interface UserService {

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(Long id);

    UserResponseDTO save(UserRequestDTO dto);

    void deleteById(Long id);

    List<RoleDTO> getRolesByUserId(Long id);
    void updateUserRoles(Long userId, List<String> roleNames);

}
