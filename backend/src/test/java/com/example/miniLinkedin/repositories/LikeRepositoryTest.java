package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.LikeEntity;
import com.example.miniLinkedin.entities.PublicationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests DAO - LikeRepository")
class LikeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LikeRepository likeRepository;

    private UserEntity alice;
    private UserEntity bob;
    private PublicationEntity publication1;
    private PublicationEntity publication2;
    private LikeEntity like1;
    private LikeEntity like2;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        bob = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.CHERCHEUR).isActive(true).build();

        entityManager.persist(alice);
        entityManager.persist(bob);

        publication1 = PublicationEntity.builder()
                .auteur(alice).contenu("Pub 1").imageUrl("img1.jpg")
                .datePublication(LocalDateTime.now()).build();

        publication2 = PublicationEntity.builder()
                .auteur(alice).contenu("Pub 2").imageUrl("img2.jpg")
                .datePublication(LocalDateTime.now()).build();

        entityManager.persist(publication1);
        entityManager.persist(publication2);

        like1 = LikeEntity.builder()
                .user(alice).publication(publication1).createdAt(LocalDateTime.now()).build();

        like2 = LikeEntity.builder()
                .user(bob).publication(publication1).createdAt(LocalDateTime.now()).build();

        entityManager.persist(like1);
        entityManager.persist(like2);
        entityManager.flush();
    }

    // ── existsByUserIdAndPublicationId ────────────────────────────────────────

    @Test
    @DisplayName("existsByUserIdAndPublicationId - doit retourner true si le like existe")
    void existsByUserIdAndPublicationId_shouldReturnTrue_whenLikeExists() {
        boolean exists = likeRepository.existsByUserIdAndPublicationId(
                alice.getId(), publication1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserIdAndPublicationId - doit retourner false si le like n'existe pas")
    void existsByUserIdAndPublicationId_shouldReturnFalse_whenLikeNotFound() {
        boolean exists = likeRepository.existsByUserIdAndPublicationId(
                alice.getId(), publication2.getId());

        assertThat(exists).isFalse();
    }

    // ── findByUserIdAndPublicationId ──────────────────────────────────────────

    @Test
    @DisplayName("findByUserIdAndPublicationId - doit retourner le like correspondant")
    void findByUserIdAndPublicationId_shouldReturnLike_whenExists() {
        Optional<LikeEntity> result = likeRepository.findByUserIdAndPublicationId(
                alice.getId(), publication1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    @DisplayName("findByUserIdAndPublicationId - doit retourner empty si le like n'existe pas")
    void findByUserIdAndPublicationId_shouldReturnEmpty_whenNotFound() {
        Optional<LikeEntity> result = likeRepository.findByUserIdAndPublicationId(
                bob.getId(), publication2.getId());

        assertThat(result).isEmpty();
    }

    // ── countByPublicationId ──────────────────────────────────────────────────

    @Test
    @DisplayName("countByPublicationId - doit compter le nombre de likes d'une publication")
    void countByPublicationId_shouldReturnCorrectCount() {
        long count = likeRepository.countByPublicationId(publication1.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countByPublicationId - doit retourner 0 si aucun like")
    void countByPublicationId_shouldReturnZero_whenNoLikes() {
        long count = likeRepository.countByPublicationId(publication2.getId());

        assertThat(count).isZero();
    }

    // ── findByPublicationId ───────────────────────────────────────────────────

    @Test
    @DisplayName("findByPublicationId - doit retourner tous les likes d'une publication")
    void findByPublicationId_shouldReturnAllLikesForPublication() {
        List<LikeEntity> likes = likeRepository.findByPublicationId(publication1.getId());

        assertThat(likes).hasSize(2);
    }

    @Test
    @DisplayName("findByPublicationId - doit retourner liste vide si aucun like")
    void findByPublicationId_shouldReturnEmpty_whenNoLikes() {
        List<LikeEntity> likes = likeRepository.findByPublicationId(publication2.getId());

        assertThat(likes).isEmpty();
    }

    // ── deleteByPublicationId ─────────────────────────────────────────────────

    @Test
    @DisplayName("deleteByPublicationId - doit supprimer tous les likes d'une publication")
    void deleteByPublicationId_shouldRemoveAllLikes() {
        likeRepository.deleteByPublicationId(publication1.getId());
        entityManager.flush();
        entityManager.clear();

        long count = likeRepository.countByPublicationId(publication1.getId());

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("deleteByPublicationId - ne doit pas supprimer les likes des autres publications")
    void deleteByPublicationId_shouldNotAffectOtherPublications() {
        // On ajoute un like sur pub2 pour s'assurer qu'il n'est pas supprimé
        LikeEntity likePub2 = LikeEntity.builder()
                .user(bob).publication(publication2).createdAt(LocalDateTime.now()).build();
        entityManager.persist(likePub2);
        entityManager.flush();

        likeRepository.deleteByPublicationId(publication1.getId());
        entityManager.flush();
        entityManager.clear();

        long count = likeRepository.countByPublicationId(publication2.getId());
        assertThat(count).isEqualTo(1);
    }
}
