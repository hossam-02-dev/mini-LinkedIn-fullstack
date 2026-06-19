package com.example.miniLinkedin.services;

import com.example.miniLinkedin.dtos.MessageResponseDto;
import com.example.miniLinkedin.entities.MessageEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.MessageMapper;
import com.example.miniLinkedin.repositories.MessageRepository;
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
@DisplayName("Tests Service - MessageService")
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageMapper messageMapper;

    @InjectMocks
    private MessageService messageService;

    private UserEntity alice;
    private UserEntity bob;
    private MessageEntity message;
    private MessageResponseDto messageResponseDto;

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

        message = MessageEntity.builder()
                .id(100L).expediteur(alice).destinataire(bob)
                .contenu("Salut Bob !").lu(false)
                .dateEnvoi(LocalDateTime.now()).build();

        messageResponseDto = MessageResponseDto.builder()
                .id(100L).expediteurId(1L).destinataireId(2L)
                .contenu("Salut Bob !").lu(false).build();
    }

    // ── sendMessage ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("sendMessage - doit créer et retourner le message")
    void sendMessage_shouldCreateAndReturnMessage() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(message);
        when(messageMapper.toDto(any(MessageEntity.class))).thenReturn(messageResponseDto);

        MessageResponseDto result = messageService.sendMessage(2L, 1L, "Salut Bob !");

        assertThat(result).isNotNull();
        assertThat(result.getContenu()).isEqualTo("Salut Bob !");
        verify(messageRepository).save(any(MessageEntity.class));
    }

    @Test
    @DisplayName("sendMessage - doit lever ResourceNotFoundException si l'expéditeur est introuvable")
    void sendMessage_shouldThrow_whenExpediteurNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.sendMessage(2L, 99L, "Bonjour"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("sendMessage - doit lever ResourceNotFoundException si le destinataire est introuvable")
    void sendMessage_shouldThrow_whenDestinataireNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.sendMessage(99L, 1L, "Bonjour"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── markMessageAsReadById ─────────────────────────────────────────────────

    @Test
    @DisplayName("markMessageAsReadById - doit marquer le message comme lu")
    void markMessageAsReadById_shouldMarkAsRead() {
        when(messageRepository.findById(100L)).thenReturn(Optional.of(message));
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(message);

        messageService.markMessageAsReadById(100L, 2L); // bob = destinataire

        assertThat(message.getLu()).isTrue();
        verify(messageRepository).save(message);
    }

    @Test
    @DisplayName("markMessageAsReadById - doit lever UnauthorizedException si l'utilisateur n'est pas le destinataire")
    void markMessageAsReadById_shouldThrow_whenNotDestinataire() {
        when(messageRepository.findById(100L)).thenReturn(Optional.of(message));

        // alice (id=1) essaie de marquer comme lu mais n'est pas le destinataire
        assertThatThrownBy(() -> messageService.markMessageAsReadById(100L, 1L))
                .isInstanceOf(UnauthorizedException.class);

        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("markMessageAsReadById - doit lever ResourceNotFoundException si le message est introuvable")
    void markMessageAsReadById_shouldThrow_whenMessageNotFound() {
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.markMessageAsReadById(999L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getMessagesNonLus ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getMessagesNonLus - doit retourner les messages non lus de l'utilisateur")
    void getMessagesNonLus_shouldReturnUnreadMessages() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(messageRepository.findByDestinataireIdAndLuFalse(2L)).thenReturn(List.of(message));
        when(messageMapper.toDtoList(anyList())).thenReturn(List.of(messageResponseDto));

        List<MessageResponseDto> result = messageService.getMessagesNonLus(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLu()).isFalse();
    }

    @Test
    @DisplayName("getMessagesNonLus - doit lever ResourceNotFoundException si l'utilisateur est introuvable")
    void getMessagesNonLus_shouldThrow_whenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> messageService.getMessagesNonLus(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── getConversation ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getConversation - doit retourner les messages de la conversation")
    void getConversation_shouldReturnConversationMessages() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(messageRepository.findConversation(1L, 2L)).thenReturn(List.of(message));
        when(messageMapper.toDtoList(anyList())).thenReturn(List.of(messageResponseDto));

        List<MessageResponseDto> result = messageService.getConversation(1L, 2L);

        assertThat(result).hasSize(1);
        verify(messageRepository).findConversation(1L, 2L);
    }

    @Test
    @DisplayName("getConversation - doit lever ResourceNotFoundException si un utilisateur est introuvable")
    void getConversation_shouldThrow_whenAnyUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> messageService.getConversation(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getConversations (alias) ───────────────────────────────────────────────

    @Test
    @DisplayName("getConversations - doit déléguer à getConversation")
    void getConversations_shouldDelegateToGetConversation() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(messageRepository.findConversation(1L, 2L)).thenReturn(List.of(message));
        when(messageMapper.toDtoList(anyList())).thenReturn(List.of(messageResponseDto));

        List<MessageResponseDto> result = messageService.getConversations(1L, 2L);

        assertThat(result).hasSize(1);
    }

    // ── deleteMessage ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteMessage - doit supprimer si l'utilisateur est l'expéditeur")
    void deleteMessage_shouldDelete_whenExpediteur() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(messageRepository.findById(100L)).thenReturn(Optional.of(message));

        messageService.deleteMessage(100L, 1L); // alice = expéditeur

        verify(messageRepository).delete(message);
    }

    @Test
    @DisplayName("deleteMessage - doit supprimer si l'utilisateur est le destinataire")
    void deleteMessage_shouldDelete_whenDestinataire() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(messageRepository.findById(100L)).thenReturn(Optional.of(message));

        messageService.deleteMessage(100L, 2L); // bob = destinataire

        verify(messageRepository).delete(message);
    }

    @Test
    @DisplayName("deleteMessage - doit lever UnauthorizedException si l'utilisateur n'est ni expéditeur ni destinataire")
    void deleteMessage_shouldThrow_whenNeitherParty() {
        UserEntity charlie = UserEntity.builder().id(3L).firstName("Charlie")
                .lastName("B").email("c@test.com").password("pwd")
                .role(Role.ETUDIANT).isActive(true).createdAt(LocalDateTime.now()).build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(charlie));
        when(messageRepository.findById(100L)).thenReturn(Optional.of(message));

        assertThatThrownBy(() -> messageService.deleteMessage(100L, 3L))
                .isInstanceOf(UnauthorizedException.class);

        verify(messageRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteMessage - doit lever ResourceNotFoundException si le message est introuvable")
    void deleteMessage_shouldThrow_whenMessageNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.deleteMessage(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("deleteMessage - doit lever ResourceNotFoundException si l'utilisateur est introuvable")
    void deleteMessage_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.deleteMessage(100L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}