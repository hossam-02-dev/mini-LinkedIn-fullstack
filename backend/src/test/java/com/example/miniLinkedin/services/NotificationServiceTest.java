package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.NotificationResponseDto;
import com.example.miniLinkedin.entities.NotificationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.NotificationMapper;
import com.example.miniLinkedin.repositories.NotificationRepository;
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
@DisplayName("Tests Service - NotificationService")
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationMapper notificationMapper;
    @Mock private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private NotificationService notificationService;

    private UserEntity alice;
    private UserEntity bob;
    private NotificationEntity notification;
    private NotificationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        alice = UserEntity.builder()
                .id(1L).firstName("Alice").lastName("M")
                .email("alice@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        bob = UserEntity.builder()
                .id(2L).firstName("Bob").lastName("D")
                .email("bob@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true)
                .createdAt(LocalDateTime.now()).build();

        notification = NotificationEntity.builder()
                .id(100L)
                .contenu("Bob a aimé votre publication")
                .type("LIKE")
                .lu(false)
                .date(LocalDateTime.now())
                .destinataire(alice)
                .declencheur(bob)
                .build();

        responseDto = NotificationResponseDto.builder()
                .id(100L).contenu("Bob a aimé votre publication")
                .type("LIKE").lu(false).destinataireId(1L).declencheurId(2L).build();
    }

    // ── getNotificationById ───────────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationById - doit retourner le DTO si la notification existe")
    void getNotificationById_shouldReturnDto_whenFound() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(responseDto);

        NotificationResponseDto result = notificationService.getNotificationById(100L);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("LIKE");
        verify(notificationRepository).findById(100L);
    }

    @Test
    @DisplayName("getNotificationById - doit lever ResourceNotFoundException si introuvable")
    void getNotificationById_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotificationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getNotificationsByDestinataireId ──────────────────────────────────────

    @Test
    @DisplayName("getNotificationsByDestinataireId - doit retourner les notifications de l'utilisateur")
    void getNotificationsByDestinataireId_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(notificationRepository.findByDestinataireIdOrderByDateDesc(1L)).thenReturn(List.of(notification));
        when(notificationMapper.toDtoList(anyList())).thenReturn(List.of(responseDto));

        List<NotificationResponseDto> result = notificationService.getNotificationsByDestinataireId(1L);

        assertThat(result).hasSize(1);
        verify(notificationRepository).findByDestinataireIdOrderByDateDesc(1L);
    }

    @Test
    @DisplayName("getNotificationsByDestinataireId - doit lever ResourceNotFoundException si destinataire introuvable")
    void getNotificationsByDestinataireId_shouldThrow_whenDestinataireNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotificationsByDestinataireId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── createNotification ────────────────────────────────────────────────────

    @Test
    @DisplayName("createNotification - doit créer la notification et l'envoyer via WebSocket")
    void createNotification_shouldCreateAndSendViaWebSocket() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(notification);
        when(notificationMapper.toDto(any(NotificationEntity.class))).thenReturn(responseDto);
        doNothing().when(webSocketNotificationService).envoyerNotification(anyLong(), any());

        NotificationResponseDto result = notificationService.createNotification(1L, 2L, "Bob a aimé votre publication", "LIKE");

        assertThat(result).isNotNull();
        verify(notificationRepository).save(any(NotificationEntity.class));
        verify(webSocketNotificationService).envoyerNotification(eq(1L), any(NotificationResponseDto.class));
    }

    @Test
    @DisplayName("createNotification - doit retourner null si destinataire == déclencheur (auto-notification ignorée)")
    void createNotification_shouldReturnNull_whenSameUser() {
        NotificationResponseDto result = notificationService.createNotification(1L, 1L, "Test", "LIKE");

        assertThat(result).isNull();
        verify(notificationRepository, never()).save(any());
        verify(webSocketNotificationService, never()).envoyerNotification(anyLong(), any());
    }

    @Test
    @DisplayName("createNotification - doit lever ResourceNotFoundException si destinataire introuvable")
    void createNotification_shouldThrow_whenDestinataireNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.createNotification(99L, 2L, "Test", "LIKE"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("createNotification - doit lever ResourceNotFoundException si déclencheur introuvable")
    void createNotification_shouldThrow_whenDeclencheurNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.createNotification(1L, 99L, "Test", "LIKE"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── markNotificationAsRead ────────────────────────────────────────────────

    @Test
    @DisplayName("markNotificationAsRead - doit marquer comme lu et retourner le DTO")
    void markNotificationAsRead_shouldMarkAsReadAndReturn() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(notification);
        when(notificationMapper.toDto(any(NotificationEntity.class))).thenReturn(responseDto);

        NotificationResponseDto result = notificationService.markNotificationAsRead(100L, 1L); // alice = destinataire

        assertThat(result).isNotNull();
        assertThat(notification.getLu()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("markNotificationAsRead - doit lever UnauthorizedException si l'utilisateur n'est pas le destinataire")
    void markNotificationAsRead_shouldThrow_whenNotDestinataire() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        // bob (id=2) n'est pas le destinataire (alice=1)
        assertThatThrownBy(() -> notificationService.markNotificationAsRead(100L, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("markNotificationAsRead - doit lever ResourceNotFoundException si notification introuvable")
    void markNotificationAsRead_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markNotificationAsRead(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── deleteNotification ────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteNotification - doit supprimer si l'utilisateur est le destinataire")
    void deleteNotification_shouldDelete_whenAuthorized() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        notificationService.deleteNotification(100L, 1L); // alice = destinataire

        verify(notificationRepository).delete(notification);
    }

    @Test
    @DisplayName("deleteNotification - doit lever UnauthorizedException si l'utilisateur n'est pas le destinataire")
    void deleteNotification_shouldThrow_whenNotDestinataire() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.deleteNotification(100L, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("autorisé");

        verify(notificationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteNotification - doit lever ResourceNotFoundException si notification introuvable")
    void deleteNotification_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.deleteNotification(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── countUnreadNotifications ──────────────────────────────────────────────

    @Test
    @DisplayName("countUnreadNotifications - doit retourner le nombre de notifications non lues")
    void countUnreadNotifications_shouldReturnCorrectCount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(notificationRepository.countByDestinataireIdAndLu(1L, false)).thenReturn(3L);

        long count = notificationService.countUnreadNotifications(1L);

        assertThat(count).isEqualTo(3L);
    }

    @Test
    @DisplayName("countUnreadNotifications - doit retourner 0 si toutes les notifications sont lues")
    void countUnreadNotifications_shouldReturnZero_whenAllRead() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(notificationRepository.countByDestinataireIdAndLu(1L, false)).thenReturn(0L);

        long count = notificationService.countUnreadNotifications(1L);

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("countUnreadNotifications - doit lever ResourceNotFoundException si utilisateur introuvable")
    void countUnreadNotifications_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.countUnreadNotifications(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}