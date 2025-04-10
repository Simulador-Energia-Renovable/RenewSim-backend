package com.renewsim.backend.user;

import org.springframework.stereotype.Service;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleDTO;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleRepository;
import com.renewsim.backend.user.dto.UserRequestDTO;
import com.renewsim.backend.user.dto.UserResponseDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDTO save(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("Eliminando usuario con ID: " + id);
        userRepository.deleteById(id);
    }

    @Override
    public List<RoleDTO> getRolesByUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> newRoles = roleNames.stream()
                .map(name -> roleRepository.findByName(RoleName.valueOf(name))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + name)))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    @Override
    public UserResponseDTO getCurrentUser(User user) {
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDTO> getUsersWithoutRoles() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles() == null || user.getRoles().isEmpty())
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}

