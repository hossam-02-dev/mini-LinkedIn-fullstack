package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.ConnexionEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.enums.StatutConnexion;
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
@DisplayName("Tests DAO - ConnexionRepository")
class ConnexionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConnexionRepository connexionRepository;

    private UserEntity alice;
    private UserEntity bob;
    private UserEntity charlie;
    private ConnexionEntity connexionAcceptee;
    private ConnexionEntity connexionEnAttente;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();

        bob = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.PROFESSEUR).isActive(true).build();

        charlie = UserEntity.builder()
                .firstName("Charlie").lastName("B").email("charlie@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.CHERCHEUR).isActive(true).build();

        entityManager.persist(alice);
        entityManager.persist(bob);
        entityManager.persist(charlie);

        connexionAcceptee = ConnexionEntity.builder()
                .demandeur(alice)
                .destinataire(bob)
                .statut(StatutConnexion.ACCEPTEE)
                .dateEnvoi(LocalDateTime.now().minusDays(2))
                .dateReponse(LocalDateTime.now().minusDays(1))
                .build();

        connexionEnAttente = ConnexionEntity.builder()
                .demandeur(charlie)
                .destinataire(alice)
                .statut(StatutConnexion.EN_ATTENTE)
                .dateEnvoi(LocalDateTime.now())
                .build();

        entityManager.persist(connexionAcceptee);
        entityManager.persist(connexionEnAttente);
        entityManager.flush();
    }

    // ── existsByDemandeurIdAndDestinataireId ──────────────────────────────────

    @Test
    @DisplayName("existsByDemandeurIdAndDestinataireId - doit retourner true si la connexion existe")
    void existsByDemandeurIdAndDestinataireId_shouldReturnTrue_whenExists() {
        boolean exists = connexionRepository.existsByDemandeurIdAndDestinataireId(
                alice.getId(), bob.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByDemandeurIdAndDestinataireId - doit retourner false si la connexion n'existe pas")
    void existsByDemandeurIdAndDestinataireId_shouldReturnFalse_whenNotExists() {
        boolean exists = connexionRepository.existsByDemandeurIdAndDestinataireId(
                bob.getId(), charlie.getId());

        assertThat(exists).isFalse();
    }

    // ── findByDemandeurIdAndDestinataireId ────────────────────────────────────

    @Test
    @DisplayName("findByDemandeurIdAndDestinataireId - doit retourner la connexion existante")
    void findByDemandeurIdAndDestinataireId_shouldReturnConnexion_whenExists() {
        Optional<ConnexionEntity> result = connexionRepository.findByDemandeurIdAndDestinataireId(
                alice.getId(), bob.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getStatut()).isEqualTo(StatutConnexion.ACCEPTEE);
    }

    @Test
    @DisplayName("findByDemandeurIdAndDestinataireId - doit retourner empty si inexistante")
    void findByDemandeurIdAndDestinataireId_shouldReturnEmpty_whenNotExists() {
        Optional<ConnexionEntity> result = connexionRepository.findByDemandeurIdAndDestinataireId(
                bob.getId(), charlie.getId());

        assertThat(result).isEmpty();
    }

    // ── findByDemandeurId ─────────────────────────────────────────────────────

    @Test
    @DisplayName("findByDemandeurId - doit retourner les connexions envoyées par l'utilisateur")
    void findByDemandeurId_shouldReturnSentConnexions() {
        List<ConnexionEntity> result = connexionRepository.findByDemandeurId(alice.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestinataire().getEmail()).isEqualTo("bob@test.com");
    }

    @Test
    @DisplayName("findByDemandeurId - doit retourner liste vide si aucune connexion envoyée")
    void findByDemandeurId_shouldReturnEmpty_whenNoneFound() {
        List<ConnexionEntity> result = connexionRepository.findByDemandeurId(bob.getId());

        assertThat(result).isEmpty();
    }

    // ── findByDestinataireId ──────────────────────────────────────────────────

    @Test
    @DisplayName("findByDestinataireId - doit retourner les connexions reçues")
    void findByDestinataireId_shouldReturnReceivedConnexions() {
        List<ConnexionEntity> result = connexionRepository.findByDestinataireId(alice.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDemandeur().getEmail()).isEqualTo("charlie@test.com");
    }

    // ── findByDestinataireIdAndStatut ─────────────────────────────────────────

    @Test
    @DisplayName("findByDestinataireIdAndStatut - doit retourner les connexions en attente pour Alice")
    void findByDestinataireIdAndStatut_shouldReturnPendingConnexions() {
        List<ConnexionEntity> result = connexionRepository.findByDestinataireIdAndStatut(
                alice.getId(), StatutConnexion.EN_ATTENTE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDemandeur().getEmail()).isEqualTo("charlie@test.com");
    }

    @Test
    @DisplayName("findByDestinataireIdAndStatut - doit retourner liste vide si aucune connexion avec ce statut")
    void findByDestinataireIdAndStatut_shouldReturnEmpty_whenNoMatch() {
        List<ConnexionEntity> result = connexionRepository.findByDestinataireIdAndStatut(
                bob.getId(), StatutConnexion.EN_ATTENTE);

        assertThat(result).isEmpty();
    }

    // ── findConnexionsAccepteesParUserId (JPQL) ───────────────────────────────

    @Test
    @DisplayName("findConnexionsAccepteesParUserId - doit retourner les connexions acceptées d'Alice (comme demandeur)")
    void findConnexionsAccepteesParUserId_shouldReturnAcceptedConnexions_forDemandeur() {
        List<ConnexionEntity> result = connexionRepository.findConnexionsAccepteesParUserId(alice.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo(StatutConnexion.ACCEPTEE);
    }

    @Test
    @DisplayName("findConnexionsAccepteesParUserId - doit retourner les connexions acceptées de Bob (comme destinataire)")
    void findConnexionsAccepteesParUserId_shouldReturnAcceptedConnexions_forDestinataire() {
        List<ConnexionEntity> result = connexionRepository.findConnexionsAccepteesParUserId(bob.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo(StatutConnexion.ACCEPTEE);
    }

    @Test
    @DisplayName("findConnexionsAccepteesParUserId - doit retourner liste vide si aucune connexion acceptée")
    void findConnexionsAccepteesParUserId_shouldReturnEmpty_whenNoneAccepted() {
        List<ConnexionEntity> result = connexionRepository.findConnexionsAccepteesParUserId(charlie.getId());

        assertThat(result).isEmpty();
    }
}
