package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.CompetenceEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Niveau;
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
@DisplayName("Tests DAO - CompetenceRepository")
class CompetenceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompetenceRepository competenceRepository;

    private UserEntity user1;
    private UserEntity user2;
    private CompetenceEntity competence1;
    private CompetenceEntity competence2;
    private CompetenceEntity competence3;

    @BeforeEach
    void setUp() {
        user1 = UserEntity.builder()
                .firstName("Alice")
                .lastName("Martin")
                .email("alice@test.com")
                .password("pwd")
                .createdAt(LocalDateTime.now())
                .role(Role.PROFESSEUR)
                .isActive(true)
                .build();
        entityManager.persist(user1);

        user2 = UserEntity.builder()
                .firstName("Bob")
                .lastName("Dupont")
                .email("bob@test.com")
                .password("pwd")
                .createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .isActive(true)
                .build();
        entityManager.persist(user2);

        competence1 = CompetenceEntity.builder()
                .nom("Java")
                .niveau(Niveau.EXPERT)
                .user(user1)
                .build();

        competence2 = CompetenceEntity.builder()
                .nom("Spring Boot")
                .niveau(Niveau.INTERMEDIAIRE)
                .user(user1)
                .build();

        competence3 = CompetenceEntity.builder()
                .nom("Python")
                .niveau(Niveau.DEBUTANT)
                .user(user2)
                .build();

        entityManager.persist(competence1);
        entityManager.persist(competence2);
        entityManager.persist(competence3);
        entityManager.flush();
    }

    // ── findByUserId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByUserId - doit retourner les compétences de l'utilisateur")
    void findByUserId_shouldReturnCompetencesForUser() {
        List<CompetenceEntity> result = competenceRepository.findByUserId(user1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CompetenceEntity::getNom)
                .containsExactlyInAnyOrder("Java", "Spring Boot");
    }

    @Test
    @DisplayName("findByUserId - doit retourner liste vide si l'utilisateur n'a pas de compétences")
    void findByUserId_shouldReturnEmpty_whenUserHasNoCompetences() {
        List<CompetenceEntity> result = competenceRepository.findByUserId(9999L);

        assertThat(result).isEmpty();
    }

    // ── existsByNomAndUserId ──────────────────────────────────────────────────

    @Test
    @DisplayName("existsByNomAndUserId - doit retourner true si la compétence existe pour cet utilisateur")
    void existsByNomAndUserId_shouldReturnTrue_whenCompetenceExists() {
        boolean exists = competenceRepository.existsByNomAndUserId("Java", user1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByNomAndUserId - doit retourner false si la compétence n'appartient pas à cet utilisateur")
    void existsByNomAndUserId_shouldReturnFalse_whenCompetenceBelongsToOtherUser() {
        boolean exists = competenceRepository.existsByNomAndUserId("Java", user2.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByNomAndUserId - doit retourner false si la compétence n'existe pas")
    void existsByNomAndUserId_shouldReturnFalse_whenCompetenceNotFound() {
        boolean exists = competenceRepository.existsByNomAndUserId("Kotlin", user1.getId());

        assertThat(exists).isFalse();
    }

    // ── deleteByUserId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteByUserId - doit supprimer toutes les compétences d'un utilisateur")
    void deleteByUserId_shouldRemoveAllCompetencesForUser() {
        competenceRepository.deleteByUserId(user1.getId());
        entityManager.flush();
        entityManager.clear();

        List<CompetenceEntity> result = competenceRepository.findByUserId(user1.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteByUserId - ne doit pas affecter les compétences des autres utilisateurs")
    void deleteByUserId_shouldNotAffectOtherUsers() {
        competenceRepository.deleteByUserId(user1.getId());
        entityManager.flush();
        entityManager.clear();

        List<CompetenceEntity> resultUser2 = competenceRepository.findByUserId(user2.getId());

        assertThat(resultUser2).hasSize(1);
        assertThat(resultUser2.get(0).getNom()).isEqualTo("Python");
    }
}
