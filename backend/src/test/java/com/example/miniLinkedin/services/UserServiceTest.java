package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.UserRequestDto;
import com.example.miniLinkedin.dtos.UserResponseDto;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.UserMapper;
import com.example.miniLinkedin.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - UserService")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserResponseDto userResponseDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("Martin")
                .email("alice@test.com").password("encodedPwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        userResponseDto = UserResponseDto.builder()
                .id(1L).firstName("Alice").lastName("Martin")
                .email("alice@test.com").build();

        userRequestDto = UserRequestDto.builder()
                .firstName("Alice").lastName("Martin")
                .email("alice@test.com").password("rawPwd")
                .roleName("USER").build();
    }

    // ── getUserById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById - doit retourner le DTO si l'utilisateur existe")
    void getUserById_shouldReturnDto_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice@test.com");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("getUserById - doit lever ResourceNotFoundException si l'utilisateur n'existe pas")
    void getUserById_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── getAllUsers ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsers - doit retourner la liste de tous les utilisateurs")
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toDtoList(anyList())).thenReturn(List.of(userResponseDto));

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        verify(userRepository).findAll();
    }

    // ── searchUsers ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("searchUsers - doit déléguer la recherche au repository")
    void searchUsers_shouldDelegateToRepository() {
        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "alice", "alice", "alice")).thenReturn(List.of(userEntity));

        List<UserEntity> result = userService.searchUsers("alice");

        assertThat(result).hasSize(1);
    }

    // ── createUser ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createUser - doit créer et retourner l'utilisateur")
    void createUser_shouldSaveAndReturnUser() {
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPwd")).thenReturn("encodedPwd");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userRequestDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("createUser - doit lever IllegalArgumentException si l'email est déjà utilisé")
    void createUser_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(userRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("alice@test.com");
    }

    // ── updateUser ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUser - doit mettre à jour et retourner l'utilisateur")
    void updateUser_shouldUpdateAndReturn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUser(1L, userRequestDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("updateUser - doit lever ResourceNotFoundException si l'utilisateur n'existe pas")
    void updateUser_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, userRequestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── desactivateUser ───────────────────────────────────────────────────────

    @Test
    @DisplayName("DesactivateUser - doit désactiver l'utilisateur")
    void desactivateUser_shouldSetActiveToFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        userService.DesactivateUser(1L);

        assertThat(userEntity.isActive()).isFalse();
        verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("DesactivateUser - doit lever ResourceNotFoundException si l'utilisateur n'existe pas")
    void desactivateUser_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.DesactivateUser(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}