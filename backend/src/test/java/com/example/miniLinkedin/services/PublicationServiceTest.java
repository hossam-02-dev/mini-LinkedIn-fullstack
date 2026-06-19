package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.PublicationRequestDto;
import com.example.miniLinkedin.dtos.PublicationResponseDto;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.PublicationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.PublicationMapper;
import com.example.miniLinkedin.repositories.CommentaireRepository;
import com.example.miniLinkedin.repositories.LikeRepository;
import com.example.miniLinkedin.repositories.PublicationRepository;
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
@DisplayName("Tests Service - PublicationService")
class PublicationServiceTest {

    @Mock private PublicationRepository publicationRepository;
    @Mock private PublicationMapper publicationMapper;
    @Mock private UserRepository userRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentaireRepository commentaireRepository;

    @InjectMocks
    private PublicationService publicationService;

    private UserEntity alice;
    private UserEntity bob;
    private ProfilEntity profilAlice;
    private PublicationEntity publication;
    private PublicationRequestDto requestDto;
    private PublicationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        profilAlice = ProfilEntity.builder()
                .id(10L).name("Alice Martin").photoUrl("photo.jpg").build();

        alice = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("Martin")
                .email("alice@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now())
                .profile(profilAlice).build();

        bob = UserEntity.builder()
                .id(2L).firstName("Bob").lastName("Dupont")
                .email("bob@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now())
                .profile(null).build();

        publication = PublicationEntity.builder()
                .id(100L).auteur(alice)
                .contenu("Ma première publication")
                .imageUrl("img.jpg")
                .datePublication(LocalDateTime.now())
                .dateMaj(LocalDateTime.now()).build();

        requestDto = PublicationRequestDto.builder()
                .contenu("Ma première publication").imageUrl("img.jpg").build();

        responseDto = PublicationResponseDto.builder()
                .id(100L).auteurId(1L)
                .contenu("Ma première publication")
                .imageUrl("img.jpg").build();
    }

    // ── getPublicationById ────────────────────────────────────────────────────

    @Test
    @DisplayName("getPublicationById - doit retourner le DTO enrichi avec nom et photo de l'auteur")
    void getPublicationById_shouldReturnEnrichedDto() {
        when(publicationRepository.findById(100L)).thenReturn(Optional.of(publication));
        when(publicationMapper.toDto(publication)).thenReturn(responseDto);

        PublicationResponseDto result = publicationService.getPublicationById(100L);

        assertThat(result).isNotNull();
        assertThat(result.getNomAuteur()).isEqualTo("Alice Martin");
        assertThat(result.getPhotoProfil()).isEqualTo("photo.jpg");
    }

    @Test
    @DisplayName("getPublicationById - doit lever ResourceNotFoundException si publication introuvable")
    void getPublicationById_shouldThrow_whenNotFound() {
        when(publicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.getPublicationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getPublicationsByAuteurId ──────────────────────────────────────────────

    @Test
    @DisplayName("getPublicationsByAuteurId - doit retourner les publications enrichies")
    void getPublicationsByAuteurId_shouldReturnEnrichedList() {
        when(publicationRepository.findByAuteurIdOrderByDatePublicationDesc(1L))
                .thenReturn(List.of(publication));
        when(publicationMapper.toDto(publication)).thenReturn(responseDto);

        List<PublicationResponseDto> result = publicationService.getPublicationsByAuteurId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNomAuteur()).isEqualTo("Alice Martin");
    }

    @Test
    @DisplayName("getPublicationsByAuteurId - doit retourner liste vide si aucune publication")
    void getPublicationsByAuteurId_shouldReturnEmpty_whenNoPublications() {
        when(publicationRepository.findByAuteurIdOrderByDatePublicationDesc(2L))
                .thenReturn(List.of());

        List<PublicationResponseDto> result = publicationService.getPublicationsByAuteurId(2L);

        assertThat(result).isEmpty();
    }

    // ── createPublication ─────────────────────────────────────────────────────

    @Test
    @DisplayName("createPublication - doit créer et retourner la publication enrichie")
    void createPublication_shouldCreateAndReturnEnriched() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(publicationRepository.save(any(PublicationEntity.class))).thenReturn(publication);
        when(publicationMapper.toDto(any(PublicationEntity.class))).thenReturn(responseDto);

        PublicationResponseDto result = publicationService.createPublication(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getNomAuteur()).isEqualTo("Alice Martin");
        assertThat(result.getPhotoProfil()).isEqualTo("photo.jpg");
        verify(publicationRepository).save(any(PublicationEntity.class));
    }

    @Test
    @DisplayName("createPublication - auteur sans profil ne doit pas lever d'exception")
    void createPublication_shouldNotThrow_whenAuteurHasNoProfile() {
        PublicationEntity pubBob = PublicationEntity.builder()
                .id(200L).auteur(bob).contenu("Pub Bob").imageUrl("img.jpg")
                .datePublication(LocalDateTime.now()).dateMaj(LocalDateTime.now()).build();

        PublicationResponseDto bobDto = PublicationResponseDto.builder()
                .id(200L).auteurId(2L).contenu("Pub Bob").build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(publicationRepository.save(any(PublicationEntity.class))).thenReturn(pubBob);
        when(publicationMapper.toDto(any(PublicationEntity.class))).thenReturn(bobDto);

        assertThatCode(() -> publicationService.createPublication(2L, requestDto))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("createPublication - doit lever ResourceNotFoundException si auteur introuvable")
    void createPublication_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.createPublication(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── updatePublication ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updatePublication - doit mettre à jour et retourner la publication")
    void updatePublication_shouldUpdateAndReturn() {
        PublicationRequestDto updateDto = PublicationRequestDto.builder()
                .contenu("Contenu modifié").imageUrl("new.jpg").build();

        when(publicationRepository.findById(100L)).thenReturn(Optional.of(publication));
        when(publicationRepository.save(any(PublicationEntity.class))).thenReturn(publication);
        when(publicationMapper.toDto(any(PublicationEntity.class))).thenReturn(responseDto);

        PublicationResponseDto result = publicationService.updatePublication(100L, 1L, updateDto);

        assertThat(result).isNotNull();
        verify(publicationRepository).save(publication);
    }

    @Test
    @DisplayName("updatePublication - doit lever UnauthorizedException si l'utilisateur n'est pas l'auteur")
    void updatePublication_shouldThrow_whenNotAuteur() {
        when(publicationRepository.findById(100L)).thenReturn(Optional.of(publication));

        // publication.auteur = alice (1), bob (2) tente de modifier
        assertThatThrownBy(() -> publicationService.updatePublication(100L, 2L, requestDto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");
    }

    @Test
    @DisplayName("updatePublication - doit lever ResourceNotFoundException si publication introuvable")
    void updatePublication_shouldThrow_whenPublicationNotFound() {
        when(publicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.updatePublication(999L, 1L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getFeed ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getFeed - doit retourner toutes les publications triées par date décroissante")
    void getFeed_shouldReturnAllPublicationsSortedDesc() {
        when(publicationRepository.findAllByOrderByDatePublicationDesc()).thenReturn(List.of(publication));
        when(publicationMapper.toDto(publication)).thenReturn(responseDto);

        List<PublicationResponseDto> result = publicationService.getFeed();

        assertThat(result).hasSize(1);
        verify(publicationRepository).findAllByOrderByDatePublicationDesc();
    }

    @Test
    @DisplayName("getFeed - doit retourner liste vide s'il n'y a aucune publication")
    void getFeed_shouldReturnEmpty_whenNoPublications() {
        when(publicationRepository.findAllByOrderByDatePublicationDesc()).thenReturn(List.of());

        List<PublicationResponseDto> result = publicationService.getFeed();

        assertThat(result).isEmpty();
    }

    // ── deletePublication ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deletePublication - doit supprimer la publication et ses likes/commentaires")
    void deletePublication_shouldDeleteWithLikesAndCommentaires() {
        when(publicationRepository.findById(100L)).thenReturn(Optional.of(publication));

        publicationService.deletePublication(100L, 1L); // alice = auteur

        verify(likeRepository).deleteByPublicationId(100L);
        verify(commentaireRepository).deleteByPublicationId(100L);
        verify(publicationRepository).delete(publication);
    }

    @Test
    @DisplayName("deletePublication - doit lever UnauthorizedException si l'utilisateur n'est pas l'auteur")
    void deletePublication_shouldThrow_whenNotAuteur() {
        when(publicationRepository.findById(100L)).thenReturn(Optional.of(publication));

        assertThatThrownBy(() -> publicationService.deletePublication(100L, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");

        verify(likeRepository, never()).deleteByPublicationId(any());
        verify(commentaireRepository, never()).deleteByPublicationId(any());
        verify(publicationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deletePublication - doit lever ResourceNotFoundException si publication introuvable")
    void deletePublication_shouldThrow_whenPublicationNotFound() {
        when(publicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.deletePublication(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}