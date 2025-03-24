package com.renewsim.backend.service;

import org.springframework.stereotype.Service;

import com.renewsim.backend.dto.UserRequestDTO;
import com.renewsim.backend.dto.UserResponseDTO;
import com.renewsim.backend.mapper.UserMapper;
import com.renewsim.backend.model.User;
import com.renewsim.backend.repositories.UserRepository;

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
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}

