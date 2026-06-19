package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.ProjetEntity;
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
@DisplayName("Tests DAO - ProjetRepository")
class ProjetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjetRepository projetRepository;

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

        // 2 projets pour Alice
        entityManager.persist(ProjetEntity.builder()
                .titre("Portfolio Web")
                .description("Mon portfolio personnel")
                .technologies("React, Spring Boot")
                .lienGithub("github.com/alice/portfolio")
                .lienDemo("alice.dev")
                .imageUrl("img.jpg")
                .dateCreation(LocalDateTime.now().minusDays(10))
                .user(alice)
                .build());

        entityManager.persist(ProjetEntity.builder()
                .titre("API REST E-commerce")
                .description("Une API e-commerce")
                .technologies("Spring Boot, PostgreSQL")
                .lienGithub("github.com/alice/ecommerce")
                .lienDemo("ecommerce.demo.com")
                .imageUrl("img.jpg")
                .dateCreation(LocalDateTime.now().minusDays(5))
                .user(alice)
                .build());

        // 1 projet pour Bob
        entityManager.persist(ProjetEntity.builder()
                .titre("Blog Personnel")
                .description("Mon blog")
                .technologies("Vue.js, Node.js")
                .lienGithub("github.com/bob/blog")
                .lienDemo("bob.blog.com")
                .imageUrl("img.jpg")
                .dateCreation(LocalDateTime.now().minusDays(3))
                .user(bob)
                .build());

        entityManager.flush();
    }

    // ── findByUserId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByUserId - doit retourner les projets de l'utilisateur")
    void findByUserId_shouldReturnProjectsForUser() {
        List<ProjetEntity> result = projetRepository.findByUserId(alice.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProjetEntity::getTitre)
                .containsExactlyInAnyOrder("Portfolio Web", "API REST E-commerce");
    }

    @Test
    @DisplayName("findByUserId - doit retourner liste vide si l'utilisateur n'a pas de projets")
    void findByUserId_shouldReturnEmpty_whenNoProjects() {
        List<ProjetEntity> result = projetRepository.findByUserId(9999L);

        assertThat(result).isEmpty();
    }

    // ── findByTitreContainingIgnoreCase ───────────────────────────────────────

    @Test
    @DisplayName("findByTitreContainingIgnoreCase - doit trouver par titre (insensible à la casse)")
    void findByTitreContainingIgnoreCase_shouldFindByTitleCaseInsensitive() {
        List<ProjetEntity> result = projetRepository.findByTitreContainingIgnoreCase("portfolio");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Portfolio Web");
    }

    @Test
    @DisplayName("findByTitreContainingIgnoreCase - doit retourner plusieurs résultats si plusieurs titres correspondent")
    void findByTitreContainingIgnoreCase_shouldReturnMultipleMatches() {
        List<ProjetEntity> result = projetRepository.findByTitreContainingIgnoreCase("api");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("API REST E-commerce");
    }

    @Test
    @DisplayName("findByTitreContainingIgnoreCase - doit retourner liste vide si aucune correspondance")
    void findByTitreContainingIgnoreCase_shouldReturnEmpty_whenNoMatch() {
        List<ProjetEntity> result = projetRepository.findByTitreContainingIgnoreCase("zzzzz");

        assertThat(result).isEmpty();
    }

    // ── findByUserIdOrderByDateCreationDesc ───────────────────────────────────

    @Test
    @DisplayName("findByUserIdOrderByDateCreationDesc - doit retourner les projets d'Alice triés DESC")
    void findByUserIdOrderByDateCreationDesc_shouldReturnSortedProjects() {
        List<ProjetEntity> result = projetRepository.findByUserIdOrderByDateCreationDesc(alice.getId());

        assertThat(result).hasSize(2);
        // Le plus récent en premier (API REST = -5 jours)
        assertThat(result.get(0).getTitre()).isEqualTo("API REST E-commerce");
        // Le plus ancien en dernier (Portfolio = -10 jours)
        assertThat(result.get(1).getTitre()).isEqualTo("Portfolio Web");
    }

    @Test
    @DisplayName("findByUserIdOrderByDateCreationDesc - doit retourner liste vide si aucun projet")
    void findByUserIdOrderByDateCreationDesc_shouldReturnEmpty_whenNoProjects() {
        List<ProjetEntity> result = projetRepository.findByUserIdOrderByDateCreationDesc(9999L);

        assertThat(result).isEmpty();
    }
}
