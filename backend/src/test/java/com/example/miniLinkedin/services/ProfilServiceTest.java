package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.ProfilRequestDto;
import com.example.miniLinkedin.dtos.ProfilResponseDto;
import com.example.miniLinkedin.dtos.ProfileStatsDto;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.ProfileViewEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.ProfilMapper;
import com.example.miniLinkedin.repositories.ProfilRepository;
import com.example.miniLinkedin.repositories.ProfileViewRepository;
import com.example.miniLinkedin.repositories.UserRepository;
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
@DisplayName("Tests Service - ProfilService")
class ProfilServiceTest {

    @Mock private ProfilRepository profilRepository;
    @Mock private ProfilMapper profilMapper;
    @Mock private UserRepository userRepository;
    @Mock private ProfileViewRepository profileViewRepository;

    @InjectMocks
    private ProfilService profilService;

    private UserEntity alice;
    private UserEntity bob;
    private ProfilEntity profilAlice;
    private ProfilRequestDto requestDto;
    private ProfilResponseDto responseDto;

    @BeforeEach
    void setUp() {
        profilAlice = ProfilEntity.builder()
                .id(10L).name("Alice Martin").ville("Paris")
                .etablissement("Uni Paris").bio("Dev Java")
                .siteWeb("alice.dev").photoUrl("photo.jpg")
                .dateNaissance(LocalDate.of(1995, 5, 15)).build();

        alice = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("Martin")
                .email("alice@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now())
                .profile(profilAlice).build();

        profilAlice.setUser(alice);

        bob = UserEntity.builder()
                .id(2L).firstName("Bob").lastName("Dupont")
                .email("bob@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now())
                .profile(null).build();

        requestDto = ProfilRequestDto.builder()
                .name("Alice Martin").ville("Lyon")
                .etablissement("INSA Lyon").bio("Dev Java & Spring")
                .siteWeb("alice-dev.fr").photoUrl("newphoto.jpg")
                .dateNaissance(LocalDate.of(1995, 5, 15)).build();

        responseDto = ProfilResponseDto.builder()
                .id(10L).name("Alice Martin").ville("Paris").build();
    }

    // ── getProfilByUserId ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getProfilByUserId - doit retourner le profil existant")
    void getProfilByUserId_shouldReturnExistingProfil() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(profilMapper.toDto(profilAlice)).thenReturn(responseDto);

        ProfilResponseDto result = profilService.getProfilByUserId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice Martin");
        verify(profilRepository, never()).save(any()); // pas de création inutile
    }

    @Test
    @DisplayName("getProfilByUserId - doit créer un profil par défaut si l'utilisateur n'en a pas")
    void getProfilByUserId_shouldCreateDefaultProfil_whenProfileIsNull() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));

        ProfilEntity createdProfil = ProfilEntity.builder()
                .id(20L).name("Bob Dupont").ville("").etablissement("")
                .bio("").siteWeb("").photoUrl("").user(bob).build();

        when(profilRepository.save(any(ProfilEntity.class))).thenReturn(createdProfil);
        when(userRepository.save(any(UserEntity.class))).thenReturn(bob);
        when(profilMapper.toDto(any(ProfilEntity.class))).thenReturn(
                ProfilResponseDto.builder().id(20L).name("Bob Dupont").build());

        ProfilResponseDto result = profilService.getProfilByUserId(2L);

        assertThat(result).isNotNull();
        verify(profilRepository).save(any(ProfilEntity.class));
    }

    @Test
    @DisplayName("getProfilByUserId - doit lever ResourceNotFoundException si utilisateur introuvable")
    void getProfilByUserId_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.getProfilByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── createProfil ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("createProfil - doit créer et retourner le profil")
    void createProfil_shouldCreateAndReturn() {
        when(profilRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(profilRepository.save(any(ProfilEntity.class))).thenReturn(profilAlice);
        when(profilMapper.toDto(any(ProfilEntity.class))).thenReturn(responseDto);

        ProfilResponseDto result = profilService.createProfil(2L, requestDto);

        assertThat(result).isNotNull();
        verify(profilRepository).save(any(ProfilEntity.class));
    }

    @Test
    @DisplayName("createProfil - doit lever IllegalStateException si l'utilisateur a déjà un profil")
    void createProfil_shouldThrow_whenProfilAlreadyExists() {
        when(profilRepository.findByUserId(1L)).thenReturn(Optional.of(profilAlice));

        assertThatThrownBy(() -> profilService.createProfil(1L, requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà un profil");

        verify(profilRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProfil - doit lever ResourceNotFoundException si utilisateur introuvable")
    void createProfil_shouldThrow_whenUserNotFound() {
        when(profilRepository.findByUserId(99L)).thenReturn(Optional.empty());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.createProfil(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── updateProfil ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfil - doit mettre à jour et retourner le profil")
    void updateProfil_shouldUpdateAndReturn() {
        when(profilRepository.findById(10L)).thenReturn(Optional.of(profilAlice));
        when(profilRepository.save(any(ProfilEntity.class))).thenReturn(profilAlice);
        when(profilMapper.toDto(any(ProfilEntity.class))).thenReturn(responseDto);

        ProfilResponseDto result = profilService.updateProfil(10L, requestDto);

        assertThat(result).isNotNull();
        verify(profilRepository).save(profilAlice);
        assertThat(profilAlice.getVille()).isEqualTo("Lyon");
    }

    @Test
    @DisplayName("updateProfil - doit lever ResourceNotFoundException si profil introuvable")
    void updateProfil_shouldThrow_whenProfilNotFound() {
        when(profilRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.updateProfil(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── uploadPhoto ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("uploadPhoto - doit mettre à jour la photo et retourner le profil")
    void uploadPhoto_shouldUpdatePhotoUrlAndReturn() {
        when(profilRepository.findById(10L)).thenReturn(Optional.of(profilAlice));
        when(profilRepository.save(any(ProfilEntity.class))).thenReturn(profilAlice);
        when(profilMapper.toDto(any(ProfilEntity.class))).thenReturn(responseDto);

        ProfilResponseDto result = profilService.uploadPhoto(10L, "newphoto.jpg");

        assertThat(result).isNotNull();
        assertThat(profilAlice.getPhotoUrl()).isEqualTo("newphoto.jpg");
        verify(profilRepository).save(profilAlice);
    }

    @Test
    @DisplayName("uploadPhoto - doit lever ResourceNotFoundException si profil introuvable")
    void uploadPhoto_shouldThrow_whenProfilNotFound() {
        when(profilRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.uploadPhoto(99L, "photo.jpg"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── recordProfileView ─────────────────────────────────────────────────────

    @Test
    @DisplayName("recordProfileView - doit enregistrer la vue si viewer != propriétaire du profil")
    void recordProfileView_shouldSaveView_whenViewerIsNotOwner() {
        when(profilRepository.findByUserId(1L)).thenReturn(Optional.of(profilAlice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(profileViewRepository.save(any(ProfileViewEntity.class))).thenReturn(null);

        profilService.recordProfileView(1L, 2L); // bob visite le profil d'alice

        verify(profileViewRepository).save(any(ProfileViewEntity.class));
    }

    @Test
    @DisplayName("recordProfileView - ne doit pas enregistrer si l'utilisateur visite son propre profil")
    void recordProfileView_shouldNotSave_whenOwnerViewsOwnProfile() {
        when(profilRepository.findByUserId(1L)).thenReturn(Optional.of(profilAlice));

        profilService.recordProfileView(1L, 1L); // alice visite son propre profil

        verify(profileViewRepository, never()).save(any());
    }

    @Test
    @DisplayName("recordProfileView - ne doit pas enregistrer si viewerId est null")
    void recordProfileView_shouldNotSave_whenViewerIdIsNull() {
        profilService.recordProfileView(1L, null);

        verify(profilRepository, never()).findByUserId(any());
        verify(profileViewRepository, never()).save(any());
    }

    @Test
    @DisplayName("recordProfileView - ne doit pas lever d'exception si le profil est introuvable")
    void recordProfileView_shouldNotThrow_whenProfilNotFound() {
        when(profilRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatCode(() -> profilService.recordProfileView(99L, 2L))
                .doesNotThrowAnyException();

        verify(profileViewRepository, never()).save(any());
    }

    // ── getMyProfileStats ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getMyProfileStats - doit retourner les statistiques du profil")
    void getMyProfileStats_shouldReturnStats() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(profileViewRepository.countViewsByProfilIdSince(eq(10L), any(LocalDateTime.class))).thenReturn(15L);

        ProfileViewEntity view = ProfileViewEntity.builder()
                .viewer(bob).viewedProfile(profilAlice).viewedAt(LocalDateTime.now()).build();

        when(profileViewRepository.findTop5RecentViewers(10L)).thenReturn(List.of(view));

        ProfileStatsDto result = profilService.getMyProfileStats(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTotalViewsLast30Days()).isEqualTo(15L);
        assertThat(result.getRecentViewers()).hasSize(1);
    }

    @Test
    @DisplayName("getMyProfileStats - doit retourner des stats vides si le profil est null")
    void getMyProfileStats_shouldReturnEmptyStats_whenProfileIsNull() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob)); // bob n'a pas de profil

        ProfileStatsDto result = profilService.getMyProfileStats(2L);

        assertThat(result).isNotNull();
        assertThat(result.getTotalViewsLast30Days()).isZero();
        verify(profileViewRepository, never()).countViewsByProfilIdSince(any(), any());
    }

    @Test
    @DisplayName("getMyProfileStats - doit lever ResourceNotFoundException si utilisateur introuvable")
    void getMyProfileStats_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.getMyProfileStats(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}