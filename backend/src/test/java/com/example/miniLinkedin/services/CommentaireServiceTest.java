package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.CommentaireRequestDto;
import com.example.miniLinkedin.dtos.CommentaireResponseDto;
import com.example.miniLinkedin.entities.CommentaireEntity;
import com.example.miniLinkedin.entities.PublicationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.CommentaireMapper;
import com.example.miniLinkedin.repositories.CommentaireRepository;
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
@DisplayName("Tests Service - CommentaireService")
class CommentaireServiceTest {

    @Mock private CommentaireRepository commentaireRepository;
    @Mock private CommentaireMapper commentaireMapper;
    @Mock private UserRepository userRepository;
    @Mock private PublicationRepository publicationRepository;

    @InjectMocks
    private CommentaireService commentaireService;

    private UserEntity alice;
    private UserEntity bob;
    private PublicationEntity publication;
    private CommentaireEntity commentaire;
    private CommentaireRequestDto requestDto;
    private CommentaireResponseDto responseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder().id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        bob = UserEntity.builder().id(2L).firstName("Bob").lastName("D")
                .email("bob@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        publication = PublicationEntity.builder().id(10L).auteur(alice)
                .contenu("Ma publication").imageUrl("img.jpg")
                .datePublication(LocalDateTime.now()).build();

        commentaire = CommentaireEntity.builder().id(100L)
                .texte("Super !").auteur(alice).publication(publication)
                .date(LocalDateTime.now()).build();

        requestDto = CommentaireRequestDto.builder()
                .texte("Super !").auteurId(1L).publicationId(10L).build();

        responseDto = CommentaireResponseDto.builder()
                .id(100L).texte("Super !").auteurId(1L).nomAuteur("Alice M").build();
    }

    // ── getCommentairesByAuteur ───────────────────────────────────────────────

    @Test
    @DisplayName("getCommentairesByAuteur - doit retourner les commentaires de l'auteur")
    void getCommentairesByAuteur_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(commentaireRepository.findByAuteurId(1L)).thenReturn(List.of(commentaire));
        when(commentaireMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<CommentaireResponseDto> result = commentaireService.getCommentairesByAuteur(1L);

        assertThat(result).hasSize(1);
        verify(commentaireRepository).findByAuteurId(1L);
    }

    @Test
    @DisplayName("getCommentairesByAuteur - doit lever ResourceNotFoundException si auteur introuvable")
    void getCommentairesByAuteur_shouldThrow_whenAuteurNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentaireService.getCommentairesByAuteur(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── getCommentairesByPublication ──────────────────────────────────────────

    @Test
    @DisplayName("getCommentairesByPublication - doit retourner les commentaires de la publication")
    void getCommentairesByPublication_shouldReturnList() {
        when(publicationRepository.existsById(10L)).thenReturn(true);
        when(commentaireRepository.findByPublicationId(10L)).thenReturn(List.of(commentaire));
        when(commentaireMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<CommentaireResponseDto> result = commentaireService.getCommentairesByPublication(10L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getCommentairesByPublication - doit lever ResourceNotFoundException si publication introuvable")
    void getCommentairesByPublication_shouldThrow_whenPublicationNotFound() {
        when(publicationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> commentaireService.getCommentairesByPublication(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── createCommentaire ─────────────────────────────────────────────────────

    @Test
    @DisplayName("createCommentaire - doit créer et retourner le commentaire")
    void createCommentaire_shouldCreateAndReturn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(publicationRepository.findById(10L)).thenReturn(Optional.of(publication));
        when(commentaireRepository.save(any(CommentaireEntity.class))).thenReturn(commentaire);
        when(commentaireMapper.toDto(commentaire)).thenReturn(responseDto);

        CommentaireResponseDto result = commentaireService.createCommentaire(10L, 1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getTexte()).isEqualTo("Super !");
        verify(commentaireRepository).save(any(CommentaireEntity.class));
    }

    @Test
    @DisplayName("createCommentaire - doit lever ResourceNotFoundException si auteur introuvable")
    void createCommentaire_shouldThrow_whenAuteurNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentaireService.createCommentaire(10L, 99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createCommentaire - doit lever ResourceNotFoundException si publication introuvable")
    void createCommentaire_shouldThrow_whenPublicationNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(publicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentaireService.createCommentaire(99L, 1L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateCommentaire ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateCommentaire - doit mettre à jour et retourner le commentaire")
    void updateCommentaire_shouldUpdateAndReturn() {
        CommentaireRequestDto updateDto = CommentaireRequestDto.builder().texte("Modifié !").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(commentaireRepository.findById(100L)).thenReturn(Optional.of(commentaire));
        when(commentaireRepository.save(any(CommentaireEntity.class))).thenReturn(commentaire);
        when(commentaireMapper.toDto(commentaire)).thenReturn(responseDto);

        CommentaireResponseDto result = commentaireService.updateCommentaire(100L, 1L, updateDto);

        assertThat(result).isNotNull();
        verify(commentaireRepository).save(commentaire);
    }

    @Test
    @DisplayName("updateCommentaire - doit lever UnauthorizedException si l'utilisateur n'est pas l'auteur")
    void updateCommentaire_shouldThrow_whenNotAuthor() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(commentaireRepository.findById(100L)).thenReturn(Optional.of(commentaire));
        // commentaire.auteur = alice (id=1), mais on essaie avec bob (id=2)

        assertThatThrownBy(() -> commentaireService.updateCommentaire(100L, 2L, requestDto))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── deleteCommentaire ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteCommentaire - doit supprimer le commentaire si l'utilisateur est l'auteur")
    void deleteCommentaire_shouldDelete_whenAuthorized() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(commentaireRepository.findById(100L)).thenReturn(Optional.of(commentaire));

        commentaireService.deleteCommentaire(100L, 1L);

        verify(commentaireRepository).delete(commentaire);
    }

    @Test
    @DisplayName("deleteCommentaire - doit lever UnauthorizedException si l'utilisateur n'est pas l'auteur")
    void deleteCommentaire_shouldThrow_whenNotAuthor() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(commentaireRepository.findById(100L)).thenReturn(Optional.of(commentaire));

        assertThatThrownBy(() -> commentaireService.deleteCommentaire(100L, 2L))
                .isInstanceOf(UnauthorizedException.class);

        verify(commentaireRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteCommentaire - doit lever ResourceNotFoundException si commentaire introuvable")
    void deleteCommentaire_shouldThrow_whenCommentaireNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(commentaireRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentaireService.deleteCommentaire(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}