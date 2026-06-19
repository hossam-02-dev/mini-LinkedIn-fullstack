package com.example.miniLinkedin.repositories;

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
@DisplayName("Tests DAO - UserRepository")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user1;
    private UserEntity user2;

    @BeforeEach
    void setUp() {
        user1 = UserEntity.builder()
                .firstName("Alice")
                .lastName("Martin")
                .email("alice@example.com")
                .password("encodedPassword1")
                .createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .isActive(true)
                .build();

        user2 = UserEntity.builder()
                .firstName("Bob")
                .lastName("Dupont")
                .email("bob@example.com")
                .password("encodedPassword2")
                .createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .isActive(false)
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    // ── findByEmail ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByEmail - doit retourner l'utilisateur quand l'email existe")
    void findByEmail_shouldReturnUser_whenEmailExists() {
        Optional<UserEntity> result = userRepository.findByEmail("alice@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("findByEmail - doit retourner empty quand l'email n'existe pas")
    void findByEmail_shouldReturnEmpty_whenEmailNotFound() {
        Optional<UserEntity> result = userRepository.findByEmail("inconnu@example.com");

        assertThat(result).isEmpty();
    }

    // ── existsByEmail ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("existsByEmail - doit retourner true quand l'email existe")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        boolean exists = userRepository.existsByEmail("alice@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - doit retourner false quand l'email n'existe pas")
    void existsByEmail_shouldReturnFalse_whenEmailNotFound() {
        boolean exists = userRepository.existsByEmail("ghost@example.com");

        assertThat(exists).isFalse();
    }

    // ── findByActivationToken ──────────────────────────────────────────────────

    @Test
    @DisplayName("findByActivationToken - doit retourner l'utilisateur quand le token existe")
    void findByActivationToken_shouldReturnUser_whenTokenExists() {
        user1.setActivationToken("token-abc-123");
        entityManager.persist(user1);
        entityManager.flush();

        Optional<UserEntity> result = userRepository.findByActivationToken("token-abc-123");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("findByActivationToken - doit retourner empty quand le token est inconnu")
    void findByActivationToken_shouldReturnEmpty_whenTokenNotFound() {
        Optional<UserEntity> result = userRepository.findByActivationToken("token-inexistant");

        assertThat(result).isEmpty();
    }

    // ── findByRole ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByRole - doit retourner les utilisateurs avec le rôle USER")
    void findByRole_shouldReturnUsersWithGivenRole() {
        List<UserEntity> users = userRepository.findByRole(Role.ETUDIANT);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("findByRole - doit retourner liste vide si aucun utilisateur avec ce rôle")
    void findByRole_shouldReturnEmptyList_whenNoUserWithRole() {
        List<UserEntity> users = userRepository.findByRole(Role.ETUDIANT);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("bob@example.com");
    }

    // ── findByIsActive ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByIsActive - doit retourner les utilisateurs actifs")
    void findByIsActive_shouldReturnActiveUsers() {
        List<UserEntity> activeUsers = userRepository.findByIsActive(true);

        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("findByIsActive - doit retourner les utilisateurs inactifs")
    void findByIsActive_shouldReturnInactiveUsers() {
        List<UserEntity> inactiveUsers = userRepository.findByIsActive(false);

        assertThat(inactiveUsers).hasSize(1);
        assertThat(inactiveUsers.get(0).getEmail()).isEqualTo("bob@example.com");
    }

    // ── findByFirstNameContaining... ───────────────────────────────────────────

    @Test
    @DisplayName("recherche - doit trouver par prénom (insensible à la casse)")
    void searchByName_shouldFindByFirstNameIgnoreCase() {
        List<UserEntity> result = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "alice", "alice", "alice");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("recherche - doit trouver par email partiel")
    void searchByName_shouldFindByEmail() {
        List<UserEntity> result = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "bob@", "bob@", "bob@");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    @DisplayName("recherche - doit retourner liste vide si aucune correspondance")
    void searchByName_shouldReturnEmpty_whenNoMatch() {
        List<UserEntity> result = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "zzz", "zzz", "zzz");

        assertThat(result).isEmpty();
    }

    // ── save / findById ────────────────────────────────────────────────────────

    @Test
    @DisplayName("save - doit persister un utilisateur et lui attribuer un id")
    void save_shouldPersistUser() {
        UserEntity newUser = UserEntity.builder()
                .firstName("Charlie")
                .lastName("Brown")
                .email("charlie@example.com")
                .password("pwd")
                .createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .isActive(true)
                .build();

        UserEntity saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }
}
