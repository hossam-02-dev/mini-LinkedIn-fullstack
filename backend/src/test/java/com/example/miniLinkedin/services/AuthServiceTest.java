package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.AuthResponseDto;
import com.example.miniLinkedin.dtos.LoginRequestDto;
import com.example.miniLinkedin.dtos.RefreshTokenRequestDto;
import com.example.miniLinkedin.dtos.RegisterRequestDto;
import com.example.miniLinkedin.dtos.UserResponseDto;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.AccountNotActivatedException;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.UserMapper;
import com.example.miniLinkedin.repositories.ProfilRepository;
import com.example.miniLinkedin.repositories.UserRepository;
import com.example.miniLinkedin.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - AuthService")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private ProfilRepository profilRepository;

    @InjectMocks
    private AuthService authService;

    private UserEntity activeUser;
    private UserEntity inactiveUser;
    private RegisterRequestDto registerDto;
    private LoginRequestDto loginDto;

    @BeforeEach
    void setUp() {
        activeUser = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("Martin")
                .email("alice@test.com").password("encodedPwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        inactiveUser = UserEntity.builder()
                .id(2L).firstName("Bob").lastName("Dupont")
                .email("bob@test.com").password("encodedPwd")
                .role(Role.ETUDIANT).isActive(false)
                .createdAt(LocalDateTime.now()).build();

        registerDto = RegisterRequestDto.builder()
                .firstName("Charlie").lastName("Brown")
                .email("charlie@test.com").password("rawPwd")
                .role(Role.ETUDIANT).build();

        loginDto = LoginRequestDto.builder()
                .email("alice@test.com").password("rawPwd").build();
    }

    // ── inscrire ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("inscrire - doit créer l'utilisateur et son profil")
    void inscrire_shouldCreateUserAndProfil() {
        when(userRepository.existsByEmail("charlie@test.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPwd")).thenReturn("encodedPwd");

        UserEntity savedUser = UserEntity.builder()
                .id(3L).firstName("Charlie").lastName("Brown")
                .email("charlie@test.com").password("encodedPwd")
                .role(Role.ETUDIANT).isActive(true).createdAt(LocalDateTime.now()).build();

        ProfilEntity savedProfil = ProfilEntity.builder()
                .id(10L).name("Charlie Brown").ville("").etablissement("").photoUrl("").build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(profilRepository.save(any(ProfilEntity.class))).thenReturn(savedProfil);
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(new UserResponseDto());

        UserResponseDto result = authService.inscrire(registerDto);

        assertThat(result).isNotNull();
        verify(userRepository, times(2)).save(any(UserEntity.class));
        verify(profilRepository).save(any(ProfilEntity.class));
    }

    @Test
    @DisplayName("inscrire - doit lever IllegalStateException si l'email est déjà utilisé")
    void inscrire_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("charlie@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.inscrire(registerDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("charlie@test.com");

        verify(userRepository, never()).save(any());
    }

    // ── activer ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("activer - doit activer le compte avec un token valide")
    void activer_shouldActivateAccount_whenTokenIsValid() {
        UserEntity inactif = UserEntity.builder()
                .id(5L).email("test@test.com").isActive(false)
                .activationToken("valid-token").role(Role.ETUDIANT)
                .createdAt(LocalDateTime.now()).firstName("X").lastName("Y")
                .password("pwd").build();

        when(userRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(inactif));
        when(userRepository.save(any(UserEntity.class))).thenReturn(inactif);

        authService.activer("valid-token");

        assertThat(inactif.isActive()).isTrue();
        assertThat(inactif.getActivationToken()).isNull();
        verify(userRepository).save(inactif);
    }

    @Test
    @DisplayName("activer - doit lever ResourceNotFoundException si le token est invalide")
    void activer_shouldThrow_whenTokenInvalid() {
        when(userRepository.findByActivationToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.activer("bad-token"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("activer - doit lever IllegalStateException si le compte est déjà actif")
    void activer_shouldThrow_whenAlreadyActive() {
        when(userRepository.findByActivationToken("token")).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> authService.activer("token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà activé");
    }

    // ── connecter ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("connecter - doit retourner les tokens si les credentials sont valides")
    void connecter_shouldReturnTokens_whenCredentialsValid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(activeUser));
        when(jwtService.generateAccessToken(activeUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(activeUser)).thenReturn("refresh-token");
        when(userRepository.save(any(UserEntity.class))).thenReturn(activeUser);

        AuthResponseDto result = authService.connecter(loginDto);

        assertThat(result.getToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    @DisplayName("connecter - doit lever AccountNotActivatedException si le compte est inactif")
    void connecter_shouldThrow_whenAccountNotActive() {
        LoginRequestDto bobLogin = LoginRequestDto.builder()
                .email("bob@test.com").password("rawPwd").build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("bob@test.com")).thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> authService.connecter(bobLogin))
                .isInstanceOf(AccountNotActivatedException.class);
    }

    @Test
    @DisplayName("connecter - doit lever ResourceNotFoundException si l'utilisateur est introuvable")
    void connecter_shouldThrow_whenUserNotFound() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        LoginRequestDto ghostLogin = LoginRequestDto.builder()
                .email("ghost@test.com").password("pwd").build();

        assertThatThrownBy(() -> authService.connecter(ghostLogin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── rafraichirToken ───────────────────────────────────────────────────────

    @Test
    @DisplayName("rafraichirToken - doit retourner de nouveaux tokens si le refresh token est valide")
    void rafraichirToken_shouldReturnNewTokens_whenRefreshTokenValid() {
        activeUser.setRefreshToken("old-refresh-token");
        activeUser.setRefreshTokenExpiry(LocalDateTime.now().plusDays(10));

        RefreshTokenRequestDto dto = new RefreshTokenRequestDto();
        dto.setRefreshToken("old-refresh-token");

        when(jwtService.extractUserName("old-refresh-token")).thenReturn("alice@test.com");
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(activeUser));
        when(jwtService.generateAccessToken(activeUser)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(activeUser)).thenReturn("new-refresh-token");
        when(userRepository.save(any(UserEntity.class))).thenReturn(activeUser);

        AuthResponseDto result = authService.rafraichirToken(dto);

        assertThat(result.getToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    @DisplayName("rafraichirToken - doit lever IllegalStateException si le refresh token est expiré")
    void rafraichirToken_shouldThrow_whenRefreshTokenExpired() {
        activeUser.setRefreshToken("expired-token");
        activeUser.setRefreshTokenExpiry(LocalDateTime.now().minusDays(1)); // expiré

        RefreshTokenRequestDto dto = new RefreshTokenRequestDto();
        dto.setRefreshToken("expired-token");

        when(jwtService.extractUserName("expired-token")).thenReturn("alice@test.com");
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> authService.rafraichirToken(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expiré");
    }

    @Test
    @DisplayName("rafraichirToken - doit lever IllegalStateException si le token ne correspond pas")
    void rafraichirToken_shouldThrow_whenTokenMismatch() {
        activeUser.setRefreshToken("stored-token");
        activeUser.setRefreshTokenExpiry(LocalDateTime.now().plusDays(10));

        RefreshTokenRequestDto dto = new RefreshTokenRequestDto();
        dto.setRefreshToken("wrong-token");

        when(jwtService.extractUserName("wrong-token")).thenReturn("alice@test.com");
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> authService.rafraichirToken(dto))
                .isInstanceOf(IllegalStateException.class);
    }
}