package com.example.miniLinkedin.repositories;

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
@DisplayName("Tests DAO - PublicationRepository")
class PublicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PublicationRepository publicationRepository;

    private UserEntity alice;
    private UserEntity bob;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        bob = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        entityManager.persist(alice);
        entityManager.persist(bob);

        // Publications d'Alice (plus récentes)
        entityManager.persist(PublicationEntity.builder()
                .auteur(alice).contenu("Publication 1 Alice").imageUrl("img.jpg")
                .datePublication(LocalDateTime.now().minusHours(3)).build());

        entityManager.persist(PublicationEntity.builder()
                .auteur(alice).contenu("Publication 2 Alice").imageUrl("img.jpg")
                .datePublication(LocalDateTime.now().minusHours(1)).build());

        // Publication de Bob
        entityManager.persist(PublicationEntity.builder()
                .auteur(bob).contenu("Publication 1 Bob").imageUrl("img.jpg")
                .datePublication(LocalDateTime.now().minusHours(2)).build());

        entityManager.flush();
    }

    // ── findByAuteurId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByAuteurId - doit retourner les publications d'Alice")
    void findByAuteurId_shouldReturnPublicationsForAuteur() {
        List<PublicationEntity> result = publicationRepository.findByAuteurId(alice.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PublicationEntity::getContenu)
                .containsExactlyInAnyOrder("Publication 1 Alice", "Publication 2 Alice");
    }

    @Test
    @DisplayName("findByAuteurId - doit retourner liste vide si l'auteur n'a pas de publications")
    void findByAuteurId_shouldReturnEmpty_whenNoPublications() {
        List<PublicationEntity> result = publicationRepository.findByAuteurId(9999L);

        assertThat(result).isEmpty();
    }

    // ── findAllByOrderByDatePublicationDesc ───────────────────────────────────

    @Test
    @DisplayName("findAllByOrderByDatePublicationDesc - doit retourner toutes les publications triées DESC")
    void findAllByOrderByDatePublicationDesc_shouldReturnAllSortedDesc() {
        List<PublicationEntity> result = publicationRepository.findAllByOrderByDatePublicationDesc();

        assertThat(result).hasSize(3);
        // La plus récente en premier (Alice pub 2 = -1h)
        assertThat(result.get(0).getContenu()).isEqualTo("Publication 2 Alice");
        // La plus ancienne en dernier (Alice pub 1 = -3h)
        assertThat(result.get(2).getContenu()).isEqualTo("Publication 1 Alice");
    }

    // ── findByAuteurIdOrderByDatePublicationDesc ──────────────────────────────

    @Test
    @DisplayName("findByAuteurIdOrderByDatePublicationDesc - doit retourner les publications d'Alice triées DESC")
    void findByAuteurIdOrderByDatePublicationDesc_shouldReturnSortedPublications() {
        List<PublicationEntity> result =
                publicationRepository.findByAuteurIdOrderByDatePublicationDesc(alice.getId());

        assertThat(result).hasSize(2);
        // La plus récente en premier
        assertThat(result.get(0).getContenu()).isEqualTo("Publication 2 Alice");
        assertThat(result.get(1).getContenu()).isEqualTo("Publication 1 Alice");
    }

    @Test
    @DisplayName("findByAuteurIdOrderByDatePublicationDesc - doit retourner liste vide si aucune publication")
    void findByAuteurIdOrderByDatePublicationDesc_shouldReturnEmpty_whenNone() {
        List<PublicationEntity> result =
                publicationRepository.findByAuteurIdOrderByDatePublicationDesc(9999L);

        assertThat(result).isEmpty();
    }
}
