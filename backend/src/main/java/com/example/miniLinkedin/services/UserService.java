package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.UserRequestDto;
import com.example.miniLinkedin.dtos.UserResponseDto;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.UserMapper;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
private final UserRepository userRepository;
private final UserMapper userMapper;
private final PasswordEncoder passwordEncoder;

@Transactional(readOnly = true)
public UserResponseDto getUserById(Long id) {
	
UserEntity user = userRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

	return userMapper.toDto(user);
	
}

public List<UserEntity> searchUsers(String query) {
    return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
}

@Transactional(readOnly = true)
public List<UserResponseDto>  getAllUsers() {
List<UserEntity> users = userRepository.findAll();
return userMapper.toDtoList(users);
	
}
@Transactional
public UserResponseDto createUser (UserRequestDto  dto) {
	
	if(userRepository.existsByEmail(dto.getEmail())) {
		throw new IllegalArgumentException("Email déjà utilisé: " + dto.getEmail());
	}
	
	UserEntity user = new UserEntity();
	user.setFirstName(dto.getFirstName());
	user.setLastName(dto.getLastName());
	user.setEmail(dto.getEmail());
	user.setActive(true);
	user.setEmail(dto.getEmail());
	user.setCreatedAt(LocalDateTime.now());
	user.setPassword(passwordEncoder.encode(dto.getPassword()));
	user.setRole(Role.valueOf(dto.getRoleName()));
	return userMapper.toDto(userRepository.save(user));
	
	
}
@Transactional 
public UserResponseDto updateUser (Long id , UserRequestDto dto) {
	
UserEntity user =	userRepository.findById(id)
	.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	user.setFirstName(dto.getFirstName());
	user.setLastName(dto.getLastName());
	user.setEmail(dto.getEmail());
	user.setCreatedAt(LocalDateTime.now());
	user.setPassword(passwordEncoder.encode(dto.getPassword()));
	user.setRole(Role.valueOf(dto.getRoleName()));
	user.setActive(true);
	return userMapper.toDto(userRepository.save(user));
	
}

@Transactional
public void DesactivateUser (Long id) {
	UserEntity user =	userRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	user.setActive(false);
	userRepository.save(user);
}
}