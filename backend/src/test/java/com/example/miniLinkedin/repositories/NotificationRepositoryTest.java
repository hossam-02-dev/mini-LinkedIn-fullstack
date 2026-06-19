package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.NotificationEntity;
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
@DisplayName("Tests DAO - NotificationRepository")
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private UserEntity alice;
    private UserEntity bob;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.PROFESSEUR).isActive(true).build();

        bob = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        entityManager.persist(alice);
        entityManager.persist(bob);

        // 2 notifications non lues pour Alice
        entityManager.persist(NotificationEntity.builder()
                .contenu("Bob a aimé votre publication")
                .type("LIKE")
                .lu(false)
                .date(LocalDateTime.now().minusMinutes(60))
                .destinataire(alice)
                .declencheur(bob)
                .build());

        entityManager.persist(NotificationEntity.builder()
                .contenu("Bob a commenté votre publication")
                .type("COMMENTAIRE")
                .lu(false)
                .date(LocalDateTime.now().minusMinutes(30))
                .destinataire(alice)
                .declencheur(bob)
                .build());

        // 1 notification lue pour Alice
        entityManager.persist(NotificationEntity.builder()
                .contenu("Bob vous a envoyé une demande de connexion")
                .type("CONNEXION")
                .lu(true)
                .date(LocalDateTime.now().minusMinutes(10))
                .destinataire(alice)
                .declencheur(bob)
                .build());

        entityManager.flush();
    }

    // ── findByDestinataireIdOrderByDateDesc ───────────────────────────────────

    @Test
    @DisplayName("findByDestinataireIdOrderByDateDesc - doit retourner toutes les notifs d'Alice triées DESC")
    void findByDestinataireIdOrderByDateDesc_shouldReturnAllNotifsSortedDesc() {
        List<NotificationEntity> result =
                notificationRepository.findByDestinataireIdOrderByDateDesc(alice.getId());

        assertThat(result).hasSize(3);
        // La plus récente en premier
        assertThat(result.get(0).getType()).isEqualTo("CONNEXION");
    }

    @Test
    @DisplayName("findByDestinataireIdOrderByDateDesc - doit retourner liste vide si aucune notification")
    void findByDestinataireIdOrderByDateDesc_shouldReturnEmpty_whenNone() {
        List<NotificationEntity> result =
                notificationRepository.findByDestinataireIdOrderByDateDesc(bob.getId());

        assertThat(result).isEmpty();
    }

    // ── findByDestinataireIdAndLu ─────────────────────────────────────────────

    @Test
    @DisplayName("findByDestinataireIdAndLu - doit retourner les notifs non lues d'Alice")
    void findByDestinataireIdAndLu_shouldReturnUnreadNotifications() {
        List<NotificationEntity> result =
                notificationRepository.findByDestinataireIdAndLu(alice.getId(), false);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(NotificationEntity::getType)
                .containsExactlyInAnyOrder("LIKE", "COMMENTAIRE");
    }

    @Test
    @DisplayName("findByDestinataireIdAndLu - doit retourner les notifs lues d'Alice")
    void findByDestinataireIdAndLu_shouldReturnReadNotifications() {
        List<NotificationEntity> result =
                notificationRepository.findByDestinataireIdAndLu(alice.getId(), true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("CONNEXION");
    }

    // ── countByDestinataireIdAndLu ────────────────────────────────────────────

    @Test
    @DisplayName("countByDestinataireIdAndLu - doit compter correctement les notifs non lues")
    void countByDestinataireIdAndLu_shouldReturnCorrectCount() {
        long count = notificationRepository.countByDestinataireIdAndLu(alice.getId(), false);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countByDestinataireIdAndLu - doit retourner 0 si toutes les notifs sont lues")
    void countByDestinataireIdAndLu_shouldReturnZero_whenNoneUnread() {
        long count = notificationRepository.countByDestinataireIdAndLu(bob.getId(), false);

        assertThat(count).isZero();
    }

    // ── deleteByDestinataireId ────────────────────────────────────────────────

    @Test
    @DisplayName("deleteByDestinataireId - doit supprimer toutes les notifs d'Alice")
    void deleteByDestinataireId_shouldRemoveAllNotificationsForUser() {
        notificationRepository.deleteByDestinataireId(alice.getId());
        entityManager.flush();
        entityManager.clear();

        List<NotificationEntity> remaining =
                notificationRepository.findByDestinataireIdOrderByDateDesc(alice.getId());

        assertThat(remaining).isEmpty();
    }
}
