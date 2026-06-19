package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.ProfileViewEntity;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests DAO - ProfileViewRepository")
class ProfileViewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileViewRepository profileViewRepository;

    private UserEntity alice;
    private UserEntity bob;
    private UserEntity charlie;
    private ProfilEntity profilAlice;

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

        charlie = UserEntity.builder()
                .firstName("Charlie").lastName("B").email("charlie@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        entityManager.persist(alice);
        entityManager.persist(bob);
        entityManager.persist(charlie);

        profilAlice = ProfilEntity.builder()
                .name("Alice Martin").ville("Paris")
                .etablissement("Uni Paris").photoUrl("img.jpg")
                .user(alice).build();
        entityManager.persist(profilAlice);

        // Bob visite le profil d'Alice il y a 3 jours
        entityManager.persist(ProfileViewEntity.builder()
                .viewer(bob)
                .viewedProfile(profilAlice)
                .viewedAt(LocalDateTime.now().minusDays(3))
                .build());

        // Charlie visite le profil d'Alice il y a 1 jour
        entityManager.persist(ProfileViewEntity.builder()
                .viewer(charlie)
                .viewedProfile(profilAlice)
                .viewedAt(LocalDateTime.now().minusDays(1))
                .build());

        // Bob revisite le profil d'Alice il y a 2 heures
        entityManager.persist(ProfileViewEntity.builder()
                .viewer(bob)
                .viewedProfile(profilAlice)
                .viewedAt(LocalDateTime.now().minusHours(2))
                .build());

        entityManager.flush();
    }

    // ── countViewsByProfilIdSince (JPQL) ──────────────────────────────────────

    @Test
    @DisplayName("countViewsByProfilIdSince - doit compter les vues depuis 2 jours")
    void countViewsByProfilIdSince_shouldReturnCorrectCount_forLast2Days() {
        LocalDateTime since = LocalDateTime.now().minusDays(2);

        long count = profileViewRepository.countViewsByProfilIdSince(profilAlice.getId(), since);

        // Charlie (-1 jour) + Bob (-2h) = 2
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countViewsByProfilIdSince - doit compter toutes les vues depuis 7 jours")
    void countViewsByProfilIdSince_shouldReturnAllViews_forLast7Days() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);

        long count = profileViewRepository.countViewsByProfilIdSince(profilAlice.getId(), since);

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("countViewsByProfilIdSince - doit retourner 0 si aucune vue dans la période")
    void countViewsByProfilIdSince_shouldReturnZero_whenNoneInPeriod() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(30);

        long count = profileViewRepository.countViewsByProfilIdSince(profilAlice.getId(), since);

        assertThat(count).isZero();
    }

    // ── existsByViewerIdAndViewedProfileIdAndViewedAtAfter ────────────────────

    @Test
    @DisplayName("existsByViewerIdAndViewedProfileIdAndViewedAtAfter - doit retourner true si vue récente existe")
    void existsByViewer_shouldReturnTrue_whenRecentViewExists() {
        LocalDateTime since = LocalDateTime.now().minusHours(3);

        boolean exists = profileViewRepository.existsByViewerIdAndViewedProfileIdAndViewedAtAfter(
                bob.getId(), profilAlice.getId(), since);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByViewerIdAndViewedProfileIdAndViewedAtAfter - doit retourner false si pas de vue récente")
    void existsByViewer_shouldReturnFalse_whenNoRecentView() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);

        boolean exists = profileViewRepository.existsByViewerIdAndViewedProfileIdAndViewedAtAfter(
                charlie.getId(), profilAlice.getId(), since);

        assertThat(exists).isFalse();
    }

    // ── findTop5RecentViewers (JPQL) ──────────────────────────────────────────

    @Test
    @DisplayName("findTop5RecentViewers - doit retourner les 5 derniers visiteurs au plus")
    void findTop5RecentViewers_shouldReturnUpTo5Viewers() {
        List<ProfileViewEntity> result = profileViewRepository.findTop5RecentViewers(profilAlice.getId());

        assertThat(result).hasSizeLessThanOrEqualTo(5);
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("findTop5RecentViewers - le premier résultat doit être la vue la plus récente")
    void findTop5RecentViewers_shouldReturnMostRecentFirst() {
        List<ProfileViewEntity> result = profileViewRepository.findTop5RecentViewers(profilAlice.getId());

        // La vue la plus récente est celle de Bob il y a 2h
        assertThat(result.get(0).getViewer().getEmail()).isEqualTo("bob@test.com");
    }
}
