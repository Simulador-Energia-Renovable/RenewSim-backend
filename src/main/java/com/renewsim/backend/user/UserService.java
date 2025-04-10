package com.renewsim.backend.user;

import java.util.List;

import com.renewsim.backend.role.RoleDTO;
import com.renewsim.backend.user.dto.UserRequestDTO;
import com.renewsim.backend.user.dto.UserResponseDTO;


public interface UserService {

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(Long id);

    UserResponseDTO save(UserRequestDTO dto);

    void deleteUser(Long id);

    List<RoleDTO> getRolesByUserId(Long id);

    void updateUserRoles(Long userId, List<String> roleNames);

    UserResponseDTO getCurrentUser(User user);

    List<UserResponseDTO> getUsersWithoutRoles();

}
