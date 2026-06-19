package com.example.miniLinkedin.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.AuthResponseDto;
import com.example.miniLinkedin.dtos.LoginRequestDto;
import com.example.miniLinkedin.dtos.RefreshTokenRequestDto;
import com.example.miniLinkedin.dtos.RegisterRequestDto;
import com.example.miniLinkedin.dtos.UserResponseDto;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.exceptions.AccountNotActivatedException;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.UserMapper;
import com.example.miniLinkedin.repositories.ProfilRepository;
import com.example.miniLinkedin.repositories.UserRepository;
import com.example.miniLinkedin.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
private final UserRepository userRepository ;
private final PasswordEncoder passwordEncoder;
private final UserMapper userMapper;
private final AuthenticationManager authenticationManager;
private final JwtService jwtService;
private final ProfilRepository profilRepository;

@Transactional
public UserResponseDto inscrire(RegisterRequestDto dto ) {
    if(userRepository.existsByEmail(dto.getEmail())) {
        throw new IllegalStateException("Un compte existe déjà avec cet email : " + dto.getEmail());    
    }
    String encodedPassword = passwordEncoder.encode(dto.getPassword());
    String activationToken = UUID.randomUUID().toString();

    UserEntity user = new UserEntity();
    user.setActivationToken(activationToken);
    user.setActive(true);
    user.setEmail(dto.getEmail());
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setPassword(encodedPassword);
    user.setRole(dto.getRole());
    user.setCreatedAt(LocalDateTime.now());

    UserEntity savedUser = userRepository.save(user);

    ProfilEntity profil = new ProfilEntity();
    profil.setUser(savedUser);
    profil.setName(savedUser.getFirstName() + " " + savedUser.getLastName());
    profil.setVille("");
    profil.setEtablissement("");
    profil.setPhotoUrl("");
    profil.setBio("Bienvenue dans mon profil ");
    profil.setSiteWeb("");
    profil.setDateNaissance(LocalDate.now());
    profil = profilRepository.save(profil);

    // ⬇️ AJOUTER CES DEUX LIGNES ⬇️
    savedUser.setProfile(profil);
    userRepository.save(savedUser);

    return userMapper.toDto(savedUser);
}
@Transactional
public void activer(String token) {
	UserEntity user = userRepository.findByActivationToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Le jeton d'activation est invalide ou expiré."));
	
	if (user.isActive()) {
        throw new IllegalStateException("Ce compte est déjà activé.");
    }
	user.setActive(true);
	user.setActivationToken(null);
	userRepository.save(user);
}
@Transactional
public AuthResponseDto connecter(LoginRequestDto dto) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
    
    UserEntity user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + dto.getEmail()));
    
    if (!user.isActive()) {
        throw new AccountNotActivatedException("Votre compte n'est pas encore activé. Veuillez vérifier vos emails.");
    }
    
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    user.setRefreshToken(refreshToken);
    user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(30));
    userRepository.save(user);

    return AuthResponseDto.builder()
            .id(user.getId())
            .token(accessToken)
            .refreshToken(refreshToken)
            .role(user.getRole())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .build();
}
@Transactional
public AuthResponseDto rafraichirToken(RefreshTokenRequestDto dto) {
    String refreshToken = dto.getRefreshToken();
    String email = jwtService.extractUserName(refreshToken);
    UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

    if (!refreshToken.equals(user.getRefreshToken()) ||
            user.getRefreshTokenExpiry() == null ||
            user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new IllegalStateException("Refresh token invalide ou expiré");
    }

    String newAccessToken = jwtService.generateAccessToken(user);
    String newRefreshToken = jwtService.generateRefreshToken(user);
    user.setRefreshToken(newRefreshToken);
    user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(30));
    userRepository.save(user);

    return AuthResponseDto.builder()
            .id(user.getId())
            .token(newAccessToken)
            .refreshToken(newRefreshToken)
            .role(user.getRole())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .build();
}


}
