package com.renewsim.backend.user;

import com.renewsim.backend.role.RoleService;
import com.renewsim.backend.user.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserUseCase {

    private final UserService userService;
    private final RoleService roleService;

    public UserUseCase(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public void updateUserRoles(Long userId, List<String> roleNames) {
        User user = userService.getByIdEntity(userId);
        user.setRoles(roleService.getRolesByNames(roleNames));
        userService.saveUser(user);
    }

    public List<UserResponseDTO> getUsersWithoutRoles() {
        return userService.getUsersWithoutRoles();
    }

    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
}

