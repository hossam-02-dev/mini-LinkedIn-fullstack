package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.MessageEntity;
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
@DisplayName("Tests DAO - MessageRepository")
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private UserEntity alice;
    private UserEntity bob;
    private UserEntity charlie;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.CHERCHEUR).isActive(true).build();

        bob = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.PROFESSEUR).isActive(true).build();

        charlie = UserEntity.builder()
                .firstName("Charlie").lastName("B").email("charlie@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        entityManager.persist(alice);
        entityManager.persist(bob);
        entityManager.persist(charlie);

        // Alice → Bob : 2 messages
        entityManager.persist(MessageEntity.builder()
                .expediteur(alice).destinataire(bob).contenu("Salut Bob !")
                .lu(false).dateEnvoi(LocalDateTime.now().minusMinutes(30)).build());

        entityManager.persist(MessageEntity.builder()
                .expediteur(bob).destinataire(alice).contenu("Salut Alice !")
                .lu(true).dateEnvoi(LocalDateTime.now().minusMinutes(20)).build());

        // Alice → Charlie : 1 message non lu
        entityManager.persist(MessageEntity.builder()
                .expediteur(alice).destinataire(charlie).contenu("Hey Charlie !")
                .lu(false).dateEnvoi(LocalDateTime.now().minusMinutes(10)).build());

        // Charlie → Alice : 1 message non lu
        entityManager.persist(MessageEntity.builder()
                .expediteur(charlie).destinataire(alice).contenu("Coucou Alice !")
                .lu(false).dateEnvoi(LocalDateTime.now().minusMinutes(5)).build());

        entityManager.flush();
    }

    // ── findConversation (JPQL) ───────────────────────────────────────────────

    @Test
    @DisplayName("findConversation - doit retourner la conversation complète entre deux utilisateurs")
    void findConversation_shouldReturnAllMessagesBetweenTwoUsers() {
        List<MessageEntity> result = messageRepository.findConversation(
                alice.getId(), bob.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findConversation - les messages doivent être triés par dateEnvoi ASC")
    void findConversation_shouldBeOrderedByDateAsc() {
        List<MessageEntity> result = messageRepository.findConversation(
                alice.getId(), bob.getId());

        assertThat(result.get(0).getExpediteur().getEmail()).isEqualTo("alice@test.com");
        assertThat(result.get(1).getExpediteur().getEmail()).isEqualTo("bob@test.com");
    }

    @Test
    @DisplayName("findConversation - doit retourner liste vide si aucun échange entre ces utilisateurs")
    void findConversation_shouldReturnEmpty_whenNoExchange() {
        List<MessageEntity> result = messageRepository.findConversation(
                bob.getId(), charlie.getId());

        assertThat(result).isEmpty();
    }

    // ── findByDestinataireIdAndLu ─────────────────────────────────────────────

    @Test
    @DisplayName("findByDestinataireIdAndLu - doit retourner les messages non lus d'Alice")
    void findByDestinataireIdAndLu_shouldReturnUnreadMessages() {
        List<MessageEntity> unread = messageRepository.findByDestinataireIdAndLu(
                alice.getId(), false);

        assertThat(unread).hasSize(1);
        assertThat(unread.get(0).getExpediteur().getEmail()).isEqualTo("charlie@test.com");
    }

    @Test
    @DisplayName("findByDestinataireIdAndLu - doit retourner les messages lus d'Alice")
    void findByDestinataireIdAndLu_shouldReturnReadMessages() {
        List<MessageEntity> read = messageRepository.findByDestinataireIdAndLu(
                alice.getId(), true);

        assertThat(read).hasSize(1);
        assertThat(read.get(0).getExpediteur().getEmail()).isEqualTo("bob@test.com");
    }

    // ── findByDestinataireIdAndLuFalse ────────────────────────────────────────

    @Test
    @DisplayName("findByDestinataireIdAndLuFalse - doit retourner les messages non lus")
    void findByDestinataireIdAndLuFalse_shouldReturnUnreadMessages() {
        List<MessageEntity> result = messageRepository.findByDestinataireIdAndLuFalse(alice.getId());

        assertThat(result).hasSize(1);
    }

    // ── countByDestinataireIdAndLu ────────────────────────────────────────────

    @Test
    @DisplayName("countByDestinataireIdAndLu - doit compter les messages non lus")
    void countByDestinataireIdAndLu_shouldReturnCorrectCount() {
        long count = messageRepository.countByDestinataireIdAndLu(alice.getId(), false);

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("countByDestinataireIdAndLu - doit retourner 0 si aucun message non lu")
    void countByDestinataireIdAndLu_shouldReturnZero_whenAllRead() {
        long count = messageRepository.countByDestinataireIdAndLu(bob.getId(), false);

        assertThat(count).isZero();
    }

    // ── findByDestinataireIdOrderByDateEnvoiDesc ──────────────────────────────

    @Test
    @DisplayName("findByDestinataireIdOrderByDateEnvoiDesc - doit retourner les messages reçus triés par date DESC")
    void findByDestinataireIdOrderByDateEnvoiDesc_shouldReturnSortedMessages() {
        List<MessageEntity> result = messageRepository.findByDestinataireIdOrderByDateEnvoiDesc(alice.getId());

        assertThat(result).hasSize(2);
        // Le message le plus récent (Charlie) doit être en premier
        assertThat(result.get(0).getExpediteur().getEmail()).isEqualTo("charlie@test.com");
    }

    // ── findByExpediteurIdOrderByDateEnvoiDesc ────────────────────────────────

    @Test
    @DisplayName("findByExpediteurIdOrderByDateEnvoiDesc - doit retourner les messages envoyés par Alice triés DESC")
    void findByExpediteurIdOrderByDateEnvoiDesc_shouldReturnSortedSentMessages() {
        List<MessageEntity> result = messageRepository.findByExpediteurIdOrderByDateEnvoiDesc(alice.getId());

        assertThat(result).hasSize(2);
    }
}
