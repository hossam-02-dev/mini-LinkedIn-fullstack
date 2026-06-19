package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.CommentaireEntity;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests DAO - CommentaireRepository")
class CommentaireRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentaireRepository commentaireRepository;

    private UserEntity auteur;
    private PublicationEntity publication1;
    private PublicationEntity publication2;
    private CommentaireEntity commentaire1;
    private CommentaireEntity commentaire2;
    private CommentaireEntity commentaire3;

    @BeforeEach
    void setUp() {
        auteur = UserEntity.builder()
                .firstName("Alice")
                .lastName("Martin")
                .email("alice@test.com")
                .password("pwd")
                .createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .isActive(true)
                .build();
        entityManager.persist(auteur);

        publication1 = PublicationEntity.builder()
                .auteur(auteur)
                .contenu("Publication 1")
                .imageUrl("img1.jpg")
                .datePublication(LocalDateTime.now())
                .build();
        entityManager.persist(publication1);

        publication2 = PublicationEntity.builder()
                .auteur(auteur)
                .contenu("Publication 2")
                .imageUrl("img2.jpg")
                .datePublication(LocalDateTime.now())
                .build();
        entityManager.persist(publication2);

        commentaire1 = CommentaireEntity.builder()
                .texte("Super article !")
                .date(LocalDateTime.now())
                .auteur(auteur)
                .publication(publication1)
                .build();

        commentaire2 = CommentaireEntity.builder()
                .texte("Je suis d'accord.")
                .date(LocalDateTime.now())
                .auteur(auteur)
                .publication(publication1)
                .build();

        commentaire3 = CommentaireEntity.builder()
                .texte("Commentaire sur pub 2")
                .date(LocalDateTime.now())
                .auteur(auteur)
                .publication(publication2)
                .build();

        entityManager.persist(commentaire1);
        entityManager.persist(commentaire2);
        entityManager.persist(commentaire3);
        entityManager.flush();
    }

    // ── findByPublicationId ───────────────────────────────────────────────────

    @Test
    @DisplayName("findByPublicationId - doit retourner les commentaires de la publication")
    void findByPublicationId_shouldReturnCommentairesForPublication() {
        List<CommentaireEntity> result = commentaireRepository.findByPublicationId(publication1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CommentaireEntity::getTexte)
                .containsExactlyInAnyOrder("Super article !", "Je suis d'accord.");
    }

    @Test
    @DisplayName("findByPublicationId - doit retourner liste vide si aucun commentaire")
    void findByPublicationId_shouldReturnEmpty_whenNoComments() {
        PublicationEntity pubVide = PublicationEntity.builder()
                .auteur(auteur)
                .contenu("Pub sans commentaire")
                .imageUrl("img.jpg")
                .datePublication(LocalDateTime.now())
                .build();
        entityManager.persist(pubVide);
        entityManager.flush();

        List<CommentaireEntity> result = commentaireRepository.findByPublicationId(pubVide.getId());

        assertThat(result).isEmpty();
    }

    // ── findByAuteurId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByAuteurId - doit retourner tous les commentaires de l'auteur")
    void findByAuteurId_shouldReturnAllCommentsByAuthor() {
        List<CommentaireEntity> result = commentaireRepository.findByAuteurId(auteur.getId());

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("findByAuteurId - doit retourner liste vide si auteur inconnu")
    void findByAuteurId_shouldReturnEmpty_whenAuthorNotFound() {
        List<CommentaireEntity> result = commentaireRepository.findByAuteurId(999L);

        assertThat(result).isEmpty();
    }

    // ── countByPublicationId ──────────────────────────────────────────────────

    @Test
    @DisplayName("countByPublicationId - doit compter correctement les commentaires")
    void countByPublicationId_shouldReturnCorrectCount() {
        long count = commentaireRepository.countByPublicationId(publication1.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countByPublicationId - doit retourner 0 si aucun commentaire")
    void countByPublicationId_shouldReturnZero_whenNoComments() {
        long count = commentaireRepository.countByPublicationId(9999L);

        assertThat(count).isZero();
    }

    // ── deleteByPublicationId ─────────────────────────────────────────────────

    @Test
    @DisplayName("deleteByPublicationId - doit supprimer tous les commentaires d'une publication")
    void deleteByPublicationId_shouldRemoveAllCommentsForPublication() {
        commentaireRepository.deleteByPublicationId(publication1.getId());
        entityManager.flush();
        entityManager.clear();

        List<CommentaireEntity> remaining = commentaireRepository.findByPublicationId(publication1.getId());

        assertThat(remaining).isEmpty();
    }

    @Test
    @DisplayName("deleteByPublicationId - ne doit pas supprimer les commentaires des autres publications")
    void deleteByPublicationId_shouldNotAffectOtherPublications() {
        commentaireRepository.deleteByPublicationId(publication1.getId());
        entityManager.flush();
        entityManager.clear();

        List<CommentaireEntity> remainingPub2 = commentaireRepository.findByPublicationId(publication2.getId());

        assertThat(remainingPub2).hasSize(1);
    }
}
