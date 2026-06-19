package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.ProjetRequestDto;
import com.example.miniLinkedin.dtos.ProjetResponseDto;
import com.example.miniLinkedin.entities.ProjetEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.ProjetMapper;
import com.example.miniLinkedin.repositories.ProjetRepository;
import com.example.miniLinkedin.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - ProjetService")
class ProjetServiceTest {

    @Mock private ProjetRepository projetRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjetMapper projetMapper;

    @InjectMocks
    private ProjetService projetService;

    private UserEntity alice;
    private UserEntity bob;
    private ProjetEntity projet;
    private ProjetRequestDto requestDto;
    private ProjetResponseDto responseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        bob = UserEntity.builder()
                .id(2L).firstName("Bob").lastName("D")
                .email("bob@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        projet = ProjetEntity.builder()
                .id(100L).titre("Portfolio Web")
                .description("Mon portfolio").technologies("React, Spring Boot")
                .lienGithub("github.com/alice/portfolio").lienDemo("alice.dev")
                .imageUrl("img.jpg").dateCreation(LocalDateTime.now())
                .dateMaj(LocalDateTime.now()).user(alice).build();

        requestDto = ProjetRequestDto.builder()
                .titre("Portfolio Web").description("Mon portfolio")
                .technologies("React, Spring Boot")
                .lienGithub("github.com/alice/portfolio")
                .lienDemo("alice.dev").imageUrl("img.jpg").build();

        responseDto = ProjetResponseDto.builder()
                .id(100L).titre("Portfolio Web")
                .description("Mon portfolio").id(1L).build();
    }

    // ── publierProjet ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("publierProjet - doit créer et retourner le projet")
    void publierProjet_shouldCreateAndReturn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(projetRepository.save(any(ProjetEntity.class))).thenReturn(projet);
        when(projetMapper.toDto(any(ProjetEntity.class))).thenReturn(responseDto);

        ProjetResponseDto result = projetService.publierProjet(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getTitre()).isEqualTo("Portfolio Web");
        verify(projetRepository).save(any(ProjetEntity.class));
    }

    @Test
    @DisplayName("publierProjet - doit lever ResourceNotFoundException si auteur introuvable")
    void publierProjet_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.publierProjet(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── updateProjet ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProjet - doit mettre à jour et retourner le projet")
    void updateProjet_shouldUpdateAndReturn() {
        when(projetRepository.findById(100L)).thenReturn(Optional.of(projet));
        when(projetRepository.save(any(ProjetEntity.class))).thenReturn(projet);
        when(projetMapper.toDto(any(ProjetEntity.class))).thenReturn(responseDto);

        ProjetResponseDto result = projetService.updateProjet(100L, 1L, requestDto);

        assertThat(result).isNotNull();
        verify(projetRepository).save(projet);
    }

    @Test
    @DisplayName("updateProjet - doit lever UnauthorizedException si l'utilisateur n'est pas le propriétaire")
    void updateProjet_shouldThrow_whenNotOwner() {
        when(projetRepository.findById(100L)).thenReturn(Optional.of(projet));

        // projet.user = alice (1), bob (2) essaie de modifier
        assertThatThrownBy(() -> projetService.updateProjet(100L, 2L, requestDto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");
    }

    @Test
    @DisplayName("updateProjet - doit lever ResourceNotFoundException si projet introuvable")
    void updateProjet_shouldThrow_whenProjetNotFound() {
        when(projetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.updateProjet(999L, 1L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getProjetById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProjetById - doit retourner le DTO si le projet existe")
    void getProjetById_shouldReturnDto_whenFound() {
        when(projetRepository.findById(100L)).thenReturn(Optional.of(projet));
        when(projetMapper.toDto(projet)).thenReturn(responseDto);

        ProjetResponseDto result = projetService.getProjetById(100L);

        assertThat(result).isNotNull();
        assertThat(result.getTitre()).isEqualTo("Portfolio Web");
    }

    @Test
    @DisplayName("getProjetById - doit lever ResourceNotFoundException si projet introuvable")
    void getProjetById_shouldThrow_whenNotFound() {
        when(projetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.getProjetById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getProjetsByUserId ────────────────────────────────────────────────────

    @Test
    @DisplayName("getProjetsByUserId - doit retourner la liste des projets de l'utilisateur")
    void getProjetsByUserId_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(projetRepository.findByUserId(1L)).thenReturn(List.of(projet));
        when(projetMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<ProjetResponseDto> result = projetService.getProjetsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Portfolio Web");
    }

    @Test
    @DisplayName("getProjetsByUserId - doit lever ResourceNotFoundException si utilisateur introuvable")
    void getProjetsByUserId_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.getProjetsByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── getAllProjets ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllProjets - doit retourner tous les projets")
    void getAllProjets_shouldReturnAllProjects() {
        when(projetRepository.findAll()).thenReturn(List.of(projet));
        when(projetMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<ProjetResponseDto> result = projetService.getAllProjets();

        assertThat(result).hasSize(1);
        verify(projetRepository).findAll();
    }

    // ── getProjectsByTitle ────────────────────────────────────────────────────

    @Test
    @DisplayName("getProjectsByTitle - doit retourner les projets correspondant au titre")
    void getProjectsByTitle_shouldReturnMatchingProjects() {
        when(projetRepository.findByTitreContainingIgnoreCase("portfolio")).thenReturn(List.of(projet));
        when(projetMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<ProjetResponseDto> result = projetService.getProjectsByTitle("portfolio");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Portfolio Web");
    }

    @Test
    @DisplayName("getProjectsByTitle - doit retourner liste vide si aucune correspondance")
    void getProjectsByTitle_shouldReturnEmpty_whenNoMatch() {
        when(projetRepository.findByTitreContainingIgnoreCase("zzz")).thenReturn(List.of());
        when(projetMapper.toDtoList(anyList())).thenReturn(List.of());

        List<ProjetResponseDto> result = projetService.getProjectsByTitle("zzz");

        assertThat(result).isEmpty();
    }

    // ── deleteProjet ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteProjet - doit supprimer si l'utilisateur est le propriétaire")
    void deleteProjet_shouldDelete_whenOwner() {
        when(projetRepository.findById(100L)).thenReturn(Optional.of(projet));

        projetService.deleteProjet(100L, 1L); // alice = propriétaire

        verify(projetRepository).delete(projet);
    }

    @Test
    @DisplayName("deleteProjet - doit lever UnauthorizedException si l'utilisateur n'est pas le propriétaire")
    void deleteProjet_shouldThrow_whenNotOwner() {
        when(projetRepository.findById(100L)).thenReturn(Optional.of(projet));

        assertThatThrownBy(() -> projetService.deleteProjet(100L, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");

        verify(projetRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteProjet - doit lever ResourceNotFoundException si projet introuvable")
    void deleteProjet_shouldThrow_whenProjetNotFound() {
        when(projetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.deleteProjet(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}