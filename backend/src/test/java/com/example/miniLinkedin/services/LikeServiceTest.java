package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.LikeResponseDto;
import com.example.miniLinkedin.entities.LikeEntity;
import com.example.miniLinkedin.entities.PublicationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.LikeMapper;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - LikeService")
class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private PublicationRepository publicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    private UserEntity alice;
    private PublicationEntity publication;
    private LikeEntity like;
    private LikeResponseDto likeResponseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        publication = PublicationEntity.builder()
                .id(10L).auteur(alice).contenu("Ma publication")
                .imageUrl("img.jpg").datePublication(LocalDateTime.now()).build();

        like = LikeEntity.builder()
                .id(100L).user(alice).publication(publication)
                .createdAt(LocalDateTime.now()).build();

        likeResponseDto = LikeResponseDto.builder()
                .id(100L).userId(1L).publicationId(10L).build();
    }

    // ── addLike ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("addLike - doit créer un like et le retourner")
    void addLike_shouldCreateAndReturnLike() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(likeRepository.findByUserIdAndPublicationId(1L, 10L)).thenReturn(Optional.empty());
        when(publicationRepository.findById(10L)).thenReturn(Optional.of(publication));
        when(likeRepository.save(any(LikeEntity.class))).thenReturn(like);
        when(likeMapper.toDto(any(LikeEntity.class))).thenReturn(likeResponseDto);

        LikeResponseDto result = likeService.addLike(10L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(likeRepository).save(any(LikeEntity.class));
    }

    @Test
    @DisplayName("addLike - doit retourner le like existant sans doublon")
    void addLike_shouldReturnExistingLike_whenAlreadyLiked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(likeRepository.findByUserIdAndPublicationId(1L, 10L)).thenReturn(Optional.of(like));
        when(likeMapper.toDto(like)).thenReturn(likeResponseDto);

        LikeResponseDto result = likeService.addLike(10L, 1L);

        assertThat(result).isNotNull();
        // Pas de nouveau save
        verify(likeRepository, never()).save(any());
    }

    @Test
    @DisplayName("addLike - doit lever IllegalArgumentException si userId est null")
    void addLike_shouldThrow_whenUserIdIsNull() {
        assertThatThrownBy(() -> likeService.addLike(10L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("authenticated");
    }

    @Test
    @DisplayName("addLike - doit lever ResourceNotFoundException si l'utilisateur est introuvable")
    void addLike_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.addLike(10L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("addLike - doit lever ResourceNotFoundException si la publication est introuvable")
    void addLike_shouldThrow_whenPublicationNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(likeRepository.findByUserIdAndPublicationId(1L, 99L)).thenReturn(Optional.empty());
        when(publicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.addLike(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── countLikesByPublication ───────────────────────────────────────────────

    @Test
    @DisplayName("countLikesByPublication - doit retourner le nombre de likes")
    void countLikesByPublication_shouldReturnCount() {
        when(publicationRepository.findById(10L)).thenReturn(Optional.of(publication));
        when(likeRepository.countByPublicationId(10L)).thenReturn(5L);

        long count = likeService.countLikesByPublication(10L);

        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("countLikesByPublication - doit lever ResourceNotFoundException si publication introuvable")
    void countLikesByPublication_shouldThrow_whenPublicationNotFound() {
        when(publicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.countLikesByPublication(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── removeLike ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("removeLike - doit supprimer le like existant")
    void removeLike_shouldDeleteLike() {
        when(likeRepository.findByUserIdAndPublicationId(1L, 10L)).thenReturn(Optional.of(like));

        likeService.removeLike(10L, 1L);

        verify(likeRepository).delete(like);
    }

    @Test
    @DisplayName("removeLike - doit lever ResourceNotFoundException si le like n'existe pas")
    void removeLike_shouldThrow_whenLikeNotFound() {
        when(likeRepository.findByUserIdAndPublicationId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.removeLike(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1")
                .hasMessageContaining("99");
    }
}