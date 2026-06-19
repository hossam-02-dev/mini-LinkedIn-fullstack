package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.FormationRequestDto;
import com.example.miniLinkedin.dtos.FormationResponseDto;
import com.example.miniLinkedin.entities.FormationEntity;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.FormationMapper;
import com.example.miniLinkedin.repositories.FormationRepository;
import com.example.miniLinkedin.repositories.ProfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - FormationService")
class FormationServiceTest {

    @Mock private FormationRepository formationRepository;
    @Mock private ProfilRepository profilRepository;
    @Mock private FormationMapper formationMapper;

    @InjectMocks
    private FormationService formationService;

    private UserEntity alice;
    private ProfilEntity profil;
    private FormationEntity formation;
    private FormationRequestDto requestDto;
    private FormationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        profil = ProfilEntity.builder()
                .id(10L).name("Alice M").ville("Paris")
                .etablissement("Uni Paris").photoUrl("img.jpg")
                .user(alice).build();

        formation = FormationEntity.builder()
                .id(100L)
                .diplome("Licence Informatique")
                .etablissement("Université Paris")
                .domaine("Informatique")
                .enCours(false)
                .dateDebut(LocalDate.of(2018, 9, 1))
                .dateFin(LocalDate.of(2021, 6, 30))
                .profil(profil)
                .build();

        requestDto = FormationRequestDto.builder()
                .diplome("Licence Informatique")
                .etablissement("Université Paris")
                .domaine("Informatique")
                .enCours(false)
                .dateDebut(LocalDate.of(2018, 9, 1))
                .dateFin(LocalDate.of(2021, 6, 30))
                .build();

        responseDto = FormationResponseDto.builder()
                .id(100L).diplome("Licence Informatique")
                .etablissement("Université Paris").build();
    }

    // ── addFormation ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("addFormation - doit créer et retourner la formation")
    void addFormation_shouldCreateAndReturn() {
        when(profilRepository.findById(10L)).thenReturn(Optional.of(profil));
        when(formationRepository.save(any(FormationEntity.class))).thenReturn(formation);
        when(formationMapper.toDto(any(FormationEntity.class))).thenReturn(responseDto);

        FormationResponseDto result = formationService.addFormation(10L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getDiplome()).isEqualTo("Licence Informatique");
        verify(formationRepository).save(any(FormationEntity.class));
    }

    @Test
    @DisplayName("addFormation - doit lever ResourceNotFoundException si le profil est introuvable")
    void addFormation_shouldThrow_whenProfilNotFound() {
        when(profilRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formationService.addFormation(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("addFormation - doit lever IllegalArgumentException si dateFin < dateDebut")
    void addFormation_shouldThrow_whenDateFinBeforeDateDebut() {
        when(profilRepository.findById(10L)).thenReturn(Optional.of(profil));

        FormationRequestDto invalidDto = FormationRequestDto.builder()
                .diplome("Master")
                .etablissement("Uni")
                .domaine("Info")
                .enCours(false)
                .dateDebut(LocalDate.of(2022, 1, 1))
                .dateFin(LocalDate.of(2020, 1, 1)) // dateFin < dateDebut
                .build();

        assertThatThrownBy(() -> formationService.addFormation(10L, invalidDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date de fin");

        verify(formationRepository, never()).save(any());
    }

    // ── updateFormation ───────────────────────────────────────────────────────

    @Test
    @DisplayName("updateFormation - doit mettre à jour et retourner la formation")
    void updateFormation_shouldUpdateAndReturn() {
        when(formationRepository.findById(100L)).thenReturn(Optional.of(formation));
        when(formationRepository.save(any(FormationEntity.class))).thenReturn(formation);
        when(formationMapper.toDto(any(FormationEntity.class))).thenReturn(responseDto);

        FormationResponseDto result = formationService.updateFormation(100L, 1L, requestDto);

        assertThat(result).isNotNull();
        verify(formationRepository).save(formation);
    }

    @Test
    @DisplayName("updateFormation - doit lever UnauthorizedException si l'utilisateur n'est pas le propriétaire")
    void updateFormation_shouldThrow_whenNotOwner() {
        when(formationRepository.findById(100L)).thenReturn(Optional.of(formation));
        // formation.profil.user.id = 1 (alice), userId = 2 (bob)

        assertThatThrownBy(() -> formationService.updateFormation(100L, 2L, requestDto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");
    }

    @Test
    @DisplayName("updateFormation - doit lever ResourceNotFoundException si la formation est introuvable")
    void updateFormation_shouldThrow_whenFormationNotFound() {
        when(formationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formationService.updateFormation(999L, 1L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("updateFormation - doit lever IllegalArgumentException si dateFin < dateDebut")
    void updateFormation_shouldThrow_whenInvalidDates() {
        when(formationRepository.findById(100L)).thenReturn(Optional.of(formation));

        FormationRequestDto invalidDto = FormationRequestDto.builder()
                .diplome("Master").etablissement("Uni").domaine("Info")
                .enCours(false)
                .dateDebut(LocalDate.of(2023, 1, 1))
                .dateFin(LocalDate.of(2021, 1, 1)) // invalide
                .build();

        assertThatThrownBy(() -> formationService.updateFormation(100L, 1L, invalidDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date de fin");
    }

    // ── getFormationsByProfilId ───────────────────────────────────────────────

    @Test
    @DisplayName("getFormationsByProfilId - doit retourner les formations du profil")
    void getFormationsByProfilId_shouldReturnList() {
        when(profilRepository.findById(10L)).thenReturn(Optional.of(profil));
        when(formationRepository.findByProfilId(10L)).thenReturn(List.of(formation));
        when(formationMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<FormationResponseDto> result = formationService.getFormationsByProfilId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDiplome()).isEqualTo("Licence Informatique");
    }

    @Test
    @DisplayName("getFormationsByProfilId - doit lever ResourceNotFoundException si profil introuvable")
    void getFormationsByProfilId_shouldThrow_whenProfilNotFound() {
        when(profilRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formationService.getFormationsByProfilId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── deleteFormation ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteFormation - doit supprimer si l'utilisateur est le propriétaire")
    void deleteFormation_shouldDelete_whenAuthorized() {
        when(formationRepository.findById(100L)).thenReturn(Optional.of(formation));

        formationService.deleteFormation(100L, 1L); // alice = propriétaire

        verify(formationRepository).delete(formation);
    }

    @Test
    @DisplayName("deleteFormation - doit lever UnauthorizedException si l'utilisateur n'est pas le propriétaire")
    void deleteFormation_shouldThrow_whenNotOwner() {
        when(formationRepository.findById(100L)).thenReturn(Optional.of(formation));

        assertThatThrownBy(() -> formationService.deleteFormation(100L, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");

        verify(formationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteFormation - doit lever ResourceNotFoundException si la formation est introuvable")
    void deleteFormation_shouldThrow_whenFormationNotFound() {
        when(formationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formationService.deleteFormation(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}