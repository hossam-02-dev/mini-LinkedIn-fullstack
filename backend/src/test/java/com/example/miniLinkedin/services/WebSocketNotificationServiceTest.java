package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.NotificationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Service - WebSocketNotificationService")
class WebSocketNotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationService webSocketNotificationService;

    private NotificationResponseDto notificationDto;

    @BeforeEach
    void setUp() {
        notificationDto = NotificationResponseDto.builder()
                .id(1L)
                .contenu("Bob a aimé votre publication")
                .type("LIKE")
                .lu(false)
                .destinataireId(10L)
                .declencheurId(20L)
                .build();
    }

    // ── envoyerNotification ───────────────────────────────────────────────────

    @Test
    @DisplayName("envoyerNotification - doit appeler convertAndSendToUser avec les bons paramètres")
    void envoyerNotification_shouldCallConvertAndSendToUser_withCorrectParams() {
        webSocketNotificationService.envoyerNotification(10L, notificationDto);

        verify(messagingTemplate).convertAndSendToUser(
                eq("10"),
                eq("/queue/notifications"),
                eq(notificationDto)
        );
    }

    @Test
    @DisplayName("envoyerNotification - doit convertir l'userId en String pour la destination")
    void envoyerNotification_shouldConvertUserIdToString() {
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> destCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        webSocketNotificationService.envoyerNotification(42L, notificationDto);

        verify(messagingTemplate).convertAndSendToUser(
                userCaptor.capture(),
                destCaptor.capture(),
                payloadCaptor.capture()
        );

        assertThat(userCaptor.getValue()).isEqualTo("42");
        assertThat(destCaptor.getValue()).isEqualTo("/queue/notifications");
        assertThat(payloadCaptor.getValue()).isEqualTo(notificationDto);
    }

    @Test
    @DisplayName("envoyerNotification - doit transmettre le bon payload DTO")
    void envoyerNotification_shouldSendCorrectPayload() {
        NotificationResponseDto autreDto = NotificationResponseDto.builder()
                .id(2L).contenu("Charlie a commenté votre publication")
                .type("COMMENTAIRE").lu(false).destinataireId(5L).build();

        webSocketNotificationService.envoyerNotification(5L, autreDto);

        verify(messagingTemplate).convertAndSendToUser(
                eq("5"),
                eq("/queue/notifications"),
                eq(autreDto)
        );
    }

    @Test
    @DisplayName("envoyerNotification - doit appeler convertAndSendToUser une seule fois par appel")
    void envoyerNotification_shouldCallConvertAndSendToUser_exactlyOnce() {
        webSocketNotificationService.envoyerNotification(10L, notificationDto);

        verify(messagingTemplate, times(1))
                .convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("envoyerNotification - doit fonctionner pour différents userIds")
    void envoyerNotification_shouldWork_forMultipleUsers() {
        webSocketNotificationService.envoyerNotification(1L, notificationDto);
        webSocketNotificationService.envoyerNotification(2L, notificationDto);
        webSocketNotificationService.envoyerNotification(100L, notificationDto);

        verify(messagingTemplate).convertAndSendToUser(eq("1"), anyString(), any());
        verify(messagingTemplate).convertAndSendToUser(eq("2"), anyString(), any());
        verify(messagingTemplate).convertAndSendToUser(eq("100"), anyString(), any());
    }
}