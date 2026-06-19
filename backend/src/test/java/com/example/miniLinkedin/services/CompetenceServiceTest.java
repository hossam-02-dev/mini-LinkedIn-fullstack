package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.CompetenceRequestDto;
import com.example.miniLinkedin.dtos.CompetenceResponseDto;
import com.example.miniLinkedin.entities.CompetenceEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Niveau;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.CompetenceMapper;
import com.example.miniLinkedin.repositories.CompetenceRepository;
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
@DisplayName("Tests Service - CompetenceService")
class CompetenceServiceTest {

    @Mock private CompetenceRepository competenceRepository;
    @Mock private UserRepository userRepository;
    @Mock private CompetenceMapper competenceMapper;

    @InjectMocks
    private CompetenceService competenceService;

    private UserEntity alice;
    private UserEntity bob;
    private CompetenceEntity competence;
    private CompetenceRequestDto requestDto;
    private CompetenceResponseDto responseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder().id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        bob = UserEntity.builder().id(2L).firstName("Bob").lastName("D")
                .email("bob@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        competence = CompetenceEntity.builder().id(10L)
                .nom("Java").niveau(Niveau.EXPERT).user(alice).build();

        requestDto = CompetenceRequestDto.builder()
                .nom("Java").niveau("EXPERT").build();

        responseDto = CompetenceResponseDto.builder()
                .id(10L).nom("Java").niveau("EXPERT").userId(1L).build();
    }

    // ── addCompetence ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("addCompetence - doit ajouter et retourner la compétence")
    void addCompetence_shouldAddAndReturn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(competenceRepository.existsByNomAndUserId("Java", 1L)).thenReturn(false);
        when(competenceRepository.save(any(CompetenceEntity.class))).thenReturn(competence);
        when(competenceMapper.toDto(any(CompetenceEntity.class))).thenReturn(responseDto);

        CompetenceResponseDto result = competenceService.addCompetence(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Java");
        verify(competenceRepository).save(any(CompetenceEntity.class));
    }

    @Test
    @DisplayName("addCompetence - doit lever IllegalStateException si la compétence existe déjà")
    void addCompetence_shouldThrow_whenCompetenceAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(competenceRepository.existsByNomAndUserId("Java", 1L)).thenReturn(true);

        assertThatThrownBy(() -> competenceService.addCompetence(1L, requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("compétence");

        verify(competenceRepository, never()).save(any());
    }

    @Test
    @DisplayName("addCompetence - doit lever ResourceNotFoundException si l'utilisateur est introuvable")
    void addCompetence_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competenceService.addCompetence(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── updateCompetence ──────────────────────────────────────────────────────

    @Test
    @DisplayName("updateCompetence - doit mettre à jour et retourner la compétence")
    void updateCompetence_shouldUpdateAndReturn() {
        CompetenceRequestDto updateDto = CompetenceRequestDto.builder()
                .nom("Kotlin").niveau("INTERMEDIAIRE").build();
        CompetenceResponseDto updatedDto = CompetenceResponseDto.builder()
                .id(10L).nom("Kotlin").niveau("INTERMEDIAIRE").build();

        when(competenceRepository.findById(10L)).thenReturn(Optional.of(competence));
        when(competenceRepository.save(any(CompetenceEntity.class))).thenReturn(competence);
        when(competenceMapper.toDto(any(CompetenceEntity.class))).thenReturn(updatedDto);

        CompetenceResponseDto result = competenceService.updateCompetence(10L, 1L, updateDto);

        assertThat(result.getNom()).isEqualTo("Kotlin");
        verify(competenceRepository).save(competence);
    }

    @Test
    @DisplayName("updateCompetence - doit lever UnauthorizedException si l'utilisateur n'est pas le propriétaire")
    void updateCompetence_shouldThrow_whenNotOwner() {
        when(competenceRepository.findById(10L)).thenReturn(Optional.of(competence));
        // competence.user = alice (id=1), mais bob (id=2) essaie de modifier

        assertThatThrownBy(() -> competenceService.updateCompetence(10L, 2L, requestDto))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("updateCompetence - doit lever ResourceNotFoundException si la compétence est introuvable")
    void updateCompetence_shouldThrow_whenCompetenceNotFound() {
        when(competenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competenceService.updateCompetence(999L, 1L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getCompetencesByUserId ────────────────────────────────────────────────

    @Test
    @DisplayName("getCompetencesByUserId - doit retourner les compétences de l'utilisateur")
    void getCompetencesByUserId_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(competenceRepository.findByUserId(1L)).thenReturn(List.of(competence));
        when(competenceMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<CompetenceResponseDto> result = competenceService.getCompetencesByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Java");
    }

    @Test
    @DisplayName("getCompetencesByUserId - doit lever ResourceNotFoundException si utilisateur introuvable")
    void getCompetencesByUserId_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competenceService.getCompetencesByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deleteCompetence ──────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteCompetence - doit supprimer la compétence si l'utilisateur est le propriétaire")
    void deleteCompetence_shouldDelete_whenAuthorized() {
        when(competenceRepository.findById(10L)).thenReturn(Optional.of(competence));

        competenceService.deleteCompetence(10L, 1L);

        verify(competenceRepository).delete(competence);
    }

    @Test
    @DisplayName("deleteCompetence - doit lever UnauthorizedException si l'utilisateur n'est pas le propriétaire")
    void deleteCompetence_shouldThrow_whenNotOwner() {
        when(competenceRepository.findById(10L)).thenReturn(Optional.of(competence));

        assertThatThrownBy(() -> competenceService.deleteCompetence(10L, 2L))
                .isInstanceOf(UnauthorizedException.class);

        verify(competenceRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteCompetence - doit lever ResourceNotFoundException si la compétence est introuvable")
    void deleteCompetence_shouldThrow_whenNotFound() {
        when(competenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competenceService.deleteCompetence(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}