package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.ConnexionResponseDto;
import com.example.miniLinkedin.entities.ConnexionEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.enums.StatutConnexion;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.ConnexionMapper;
import com.example.miniLinkedin.repositories.ConnexionRepository;
import com.example.miniLinkedin.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - ConnexionService")
class ConnexionServiceTest {

    @Mock private ConnexionRepository connexionRepository;
    @Mock private NotificationService notificationService;
    @Mock private ConnexionMapper connexionMapper;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ConnexionService connexionService;

    private UserEntity alice;
    private UserEntity bob;
    private UserEntity charlie;
    private ConnexionEntity connexionEnAttente;
    private ConnexionEntity connexionAcceptee;
    private ConnexionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder().id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        bob = UserEntity.builder().id(2L).firstName("Bob").lastName("D")
                .email("bob@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        charlie = UserEntity.builder().id(3L).firstName("Charlie").lastName("B")
                .email("charlie@test.com").password("pwd").role(Role.ETUDIANT)
                .isActive(true).createdAt(LocalDateTime.now()).build();

        connexionEnAttente = ConnexionEntity.builder().id(100L)
                .demandeur(alice).destinataire(bob)
                .statut(StatutConnexion.EN_ATTENTE)
                .dateEnvoi(LocalDateTime.now()).build();

        connexionAcceptee = ConnexionEntity.builder().id(101L)
                .demandeur(alice).destinataire(charlie)
                .statut(StatutConnexion.ACCEPTEE)
                .dateEnvoi(LocalDateTime.now().minusDays(2))
                .dateReponse(LocalDateTime.now().minusDays(1)).build();

        responseDto = ConnexionResponseDto.builder()
                .id(100L).statutConnexion("EN_ATTENTE")
                .demandeurId(1L).destinataireId(2L).build();
    }

    // ── envoyerDemande ────────────────────────────────────────────────────────

    @Test
    @DisplayName("envoyerDemande - doit créer la demande de connexion")
    void envoyerDemande_shouldCreateConnexion() {
        when(connexionRepository.existsByDemandeurIdAndDestinataireId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(connexionRepository.save(any(ConnexionEntity.class))).thenReturn(connexionEnAttente);
        when(connexionMapper.toDto(connexionEnAttente)).thenReturn(responseDto);
        when(notificationService.createNotification(anyLong(), anyLong(), anyString(), anyString())).thenReturn(null);

        ConnexionResponseDto result = connexionService.envoyerDemande(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getStatutConnexion()).isEqualTo("EN_ATTENTE");
        verify(connexionRepository).save(any(ConnexionEntity.class));
        verify(notificationService).createNotification(eq(2L), eq(1L), anyString(), eq("CONNEXION"));
    }

    @Test
    @DisplayName("envoyerDemande - doit lever IllegalStateException si demandeur == destinataire")
    void envoyerDemande_shouldThrow_whenSameUser() {
        assertThatThrownBy(() -> connexionService.envoyerDemande(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("destinataire");
    }

    @Test
    @DisplayName("envoyerDemande - doit lever IllegalStateException si la demande existe déjà")
    void envoyerDemande_shouldThrow_whenAlreadySent() {
        when(connexionRepository.existsByDemandeurIdAndDestinataireId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> connexionService.envoyerDemande(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà été envoyée");
    }

    @Test
    @DisplayName("envoyerDemande - doit lever ResourceNotFoundException si demandeur introuvable")
    void envoyerDemande_shouldThrow_whenDemandeurNotFound() {
        when(connexionRepository.existsByDemandeurIdAndDestinataireId(99L, 2L)).thenReturn(false);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> connexionService.envoyerDemande(99L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── accepterConnexion ─────────────────────────────────────────────────────

    @Test
    @DisplayName("accepterConnexion - doit accepter la connexion et notifier le demandeur")
    void accepterConnexion_shouldAcceptAndNotify() {
        ConnexionResponseDto acceptedDto = ConnexionResponseDto.builder()
                .id(100L).statutConnexion("ACCEPTEE").build();

        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));
        when(connexionRepository.save(any(ConnexionEntity.class))).thenReturn(connexionEnAttente);
        when(connexionMapper.toDto(any(ConnexionEntity.class))).thenReturn(acceptedDto);
        when(notificationService.createNotification(anyLong(), anyLong(), anyString(), anyString())).thenReturn(null);

        ConnexionResponseDto result = connexionService.accepterConnexion(100L, 2L); // bob = destinataire

        assertThat(result.getStatutConnexion()).isEqualTo("ACCEPTEE");
        verify(notificationService).createNotification(eq(1L), eq(2L), anyString(), eq("CONNEXION"));
    }

    @Test
    @DisplayName("accepterConnexion - doit lever UnauthorizedException si l'utilisateur n'est pas le destinataire")
    void accepterConnexion_shouldThrow_whenNotDestinataire() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        // charlie (id=3) tente d'accepter mais n'est pas le destinataire (bob=2)
        assertThatThrownBy(() -> connexionService.accepterConnexion(100L, 3L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("accepterConnexion - doit lever IllegalStateException si la demande n'est plus EN_ATTENTE")
    void accepterConnexion_shouldThrow_whenNotPending() {
        when(connexionRepository.findById(101L)).thenReturn(Optional.of(connexionAcceptee));

        assertThatThrownBy(() -> connexionService.accepterConnexion(101L, 3L))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── refuserDemande ────────────────────────────────────────────────────────

    @Test
    @DisplayName("refuserDemande - doit refuser la connexion")
    void refuserDemande_shouldSetStatutRefusee() {
        ConnexionResponseDto refusedDto = ConnexionResponseDto.builder()
                .id(100L).statutConnexion("REFUSEE").build();

        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));
        when(connexionRepository.save(any(ConnexionEntity.class))).thenReturn(connexionEnAttente);
        when(connexionMapper.toDto(any(ConnexionEntity.class))).thenReturn(refusedDto);

        ConnexionResponseDto result = connexionService.refuserDemande(100L, 2L); // bob est destinataire

        assertThat(result.getStatutConnexion()).isEqualTo("REFUSEE");
    }

    @Test
    @DisplayName("refuserDemande - doit lever UnauthorizedException si l'utilisateur n'est pas le destinataire")
    void refuserDemande_shouldThrow_whenNotDestinataire() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        assertThatThrownBy(() -> connexionService.refuserDemande(100L, 3L))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── annulerDemande ────────────────────────────────────────────────────────

    @Test
    @DisplayName("annulerDemande - doit supprimer la connexion si l'utilisateur est le demandeur")
    void annulerDemande_shouldDelete_whenDemandeur() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        connexionService.annulerDemande(100L, 1L); // alice = demandeur

        verify(connexionRepository).delete(connexionEnAttente);
    }

    @Test
    @DisplayName("annulerDemande - doit lever UnauthorizedException si l'utilisateur n'est pas le demandeur")
    void annulerDemande_shouldThrow_whenNotDemandeur() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        assertThatThrownBy(() -> connexionService.annulerDemande(100L, 2L))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── getConnexionsAcceptees ────────────────────────────────────────────────

    @Test
    @DisplayName("getConnexionsAcceptees - doit retourner les connexions acceptées")
    void getConnexionsAcceptees_shouldReturnList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(connexionRepository.findConnexionsAccepteesParUserId(1L)).thenReturn(List.of(connexionAcceptee));
        when(connexionMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<ConnexionResponseDto> result = connexionService.getConnexionsAcceptees(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getConnexionsAcceptees - doit lever ResourceNotFoundException si utilisateur introuvable")
    void getConnexionsAcceptees_shouldThrow_whenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> connexionService.getConnexionsAcceptees(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getDemandesRecues ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getDemandesRecues - doit retourner les demandes reçues en attente")
    void getDemandesRecues_shouldReturnPendingRequests() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(connexionRepository.findByDestinataireIdAndStatut(2L, StatutConnexion.EN_ATTENTE))
                .thenReturn(List.of(connexionEnAttente));
        when(connexionMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<ConnexionResponseDto> result = connexionService.getDemandesRecues(2L);

        assertThat(result).hasSize(1);
    }

    // ── supprimerConnexion ────────────────────────────────────────────────────

    @Test
    @DisplayName("supprimerConnexion - doit supprimer si l'utilisateur est le demandeur")
    void supprimerConnexion_shouldDelete_whenDemandeur() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        connexionService.supprimerConnexion(100L, 1L);

        verify(connexionRepository).delete(connexionEnAttente);
    }

    @Test
    @DisplayName("supprimerConnexion - doit supprimer si l'utilisateur est le destinataire")
    void supprimerConnexion_shouldDelete_whenDestinataire() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        connexionService.supprimerConnexion(100L, 2L); // bob = destinataire

        verify(connexionRepository).delete(connexionEnAttente);
    }

    @Test
    @DisplayName("supprimerConnexion - doit lever UnauthorizedException si l'utilisateur n'est ni demandeur ni destinataire")
    void supprimerConnexion_shouldThrow_whenNeitherParty() {
        when(connexionRepository.findById(100L)).thenReturn(Optional.of(connexionEnAttente));

        assertThatThrownBy(() -> connexionService.supprimerConnexion(100L, 3L))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── getConnectionStatus ───────────────────────────────────────────────────

    @Test
    @DisplayName("getConnectionStatus - doit retourner 'pending' si l'utilisateur courant est le demandeur")
    void getConnectionStatus_shouldReturnPending_whenCurrentUserIsDemandeur() {
        when(connexionRepository.findByDemandeurIdAndDestinataireId(1L, 2L))
                .thenReturn(Optional.of(connexionEnAttente));

        String status = connexionService.getConnectionStatus(1L, 2L);

        assertThat(status).isEqualTo("pending");
    }

    @Test
    @DisplayName("getConnectionStatus - doit retourner 'default' si aucune connexion n'existe")
    void getConnectionStatus_shouldReturnDefault_whenNoConnexion() {
        when(connexionRepository.findByDemandeurIdAndDestinataireId(1L, 3L)).thenReturn(Optional.empty());
        when(connexionRepository.findByDemandeurIdAndDestinataireId(3L, 1L)).thenReturn(Optional.empty());

        String status = connexionService.getConnectionStatus(1L, 3L);

        assertThat(status).isEqualTo("default");
    }

    @Test
    @DisplayName("getConnectionStatus - doit retourner 'connected' si la connexion est acceptée")
    void getConnectionStatus_shouldReturnConnected_whenAccepted() {
        when(connexionRepository.findByDemandeurIdAndDestinataireId(1L, 3L))
                .thenReturn(Optional.of(connexionAcceptee));

        String status = connexionService.getConnectionStatus(1L, 3L);

        assertThat(status).isEqualTo("connected");
    }
}