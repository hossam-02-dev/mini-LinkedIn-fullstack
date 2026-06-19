package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests DAO - ProfilRepository")
class ProfilRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfilRepository profilRepository;

    private UserEntity alice;
    private UserEntity bob;
    private ProfilEntity profilAlice;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();
        entityManager.persist(alice);

        bob = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();
        entityManager.persist(bob);

        profilAlice = ProfilEntity.builder()
                .name("Alice Martin")
                .ville("Paris")
                .etablissement("Université Paris")
                .bio("Développeuse Java")
                .photoUrl("photo-alice.jpg")
                .user(alice)
                .build();
        entityManager.persist(profilAlice);
        entityManager.flush();
    }

    // ── findByUserId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByUserId - doit retourner le profil de l'utilisateur")
    void findByUserId_shouldReturnProfile_whenExists() {
        Optional<ProfilEntity> result = profilRepository.findByUserId(alice.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice Martin");
        assertThat(result.get().getVille()).isEqualTo("Paris");
    }

    @Test
    @DisplayName("findByUserId - doit retourner empty si l'utilisateur n'a pas de profil")
    void findByUserId_shouldReturnEmpty_whenNoProfile() {
        Optional<ProfilEntity> result = profilRepository.findByUserId(bob.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserId - doit retourner empty pour un id inconnu")
    void findByUserId_shouldReturnEmpty_whenUserNotFound() {
        Optional<ProfilEntity> result = profilRepository.findByUserId(9999L);

        assertThat(result).isEmpty();
    }

    // ── existsByUserId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("existsByUserId - doit retourner true si le profil existe")
    void existsByUserId_shouldReturnTrue_whenProfileExists() {
        boolean exists = profilRepository.existsByUserId(alice.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserId - doit retourner false si l'utilisateur n'a pas de profil")
    void existsByUserId_shouldReturnFalse_whenNoProfile() {
        boolean exists = profilRepository.existsByUserId(bob.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByUserId - doit retourner false pour un id inexistant")
    void existsByUserId_shouldReturnFalse_whenUserNotFound() {
        boolean exists = profilRepository.existsByUserId(9999L);

        assertThat(exists).isFalse();
    }

    // ── save / findById ────────────────────────────────────────────────────────

    @Test
    @DisplayName("save - doit persister un profil et le retrouver")
    void save_shouldPersistProfile() {
        ProfilEntity newProfil = ProfilEntity.builder()
                .name("Bob Dupont")
                .ville("Lyon")
                .etablissement("INSA Lyon")
                .photoUrl("photo-bob.jpg")
                .user(bob)
                .build();

        ProfilEntity saved = profilRepository.save(newProfil);

        assertThat(saved.getId()).isNotNull();
        assertThat(profilRepository.findById(saved.getId())).isPresent();
    }
}
