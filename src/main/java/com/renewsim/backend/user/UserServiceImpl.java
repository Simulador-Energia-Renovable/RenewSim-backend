package com.renewsim.backend.user;

import org.springframework.stereotype.Service;

import com.renewsim.backend.role.RoleDTO;

import com.renewsim.backend.user.dto.UserRequestDTO;
import com.renewsim.backend.user.dto.UserResponseDTO;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getById(Long id) {
        User user = getByIdEntity(id);
        return userMapper.toResponseDto(user);
    }

    @Override
    public User getByIdEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserResponseDTO save(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<RoleDTO> getRolesByUserId(Long id) {
        User user = getByIdEntity(id);
        return user.getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
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
