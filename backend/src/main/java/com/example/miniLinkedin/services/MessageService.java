package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.MessageResponseDto;
import com.example.miniLinkedin.entities.MessageEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.MessageMapper;
import com.example.miniLinkedin.repositories.MessageRepository;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
	
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageResponseDto sendMessage(Long destinataireId, Long expediteurId, String contenu) {
        UserEntity expediteur = userRepository.findById(expediteurId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + expediteurId));
        UserEntity destinataire = userRepository.findById(destinataireId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + destinataireId));
        MessageEntity message = new MessageEntity();
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setContenu(contenu);
        message.setLu(false);
        message.setDateEnvoi(LocalDateTime.now());
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Transactional
    public void markMessageAsReadById(Long messageId, Long userId) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        if (!message.getDestinataire().getId().equals(userId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à marquer ce message comme lu");
        }
        message.setLu(true);
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessagesNonLus(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + userId);
        }
        List<MessageEntity> messages = messageRepository.findByDestinataireIdAndLuFalse(userId);
        return messageMapper.toDtoList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getConversation(Long userId1, Long userId2) {
        if (!userRepository.existsById(userId1) || !userRepository.existsById(userId2)) {
            throw new ResourceNotFoundException("Un ou plusieurs utilisateurs non trouvés");
        }
        List<MessageEntity> conversation = messageRepository.findConversation(userId1, userId2);
        return messageMapper.toDtoList(conversation);
    }

    // Méthode ajoutée pour correspondre à l'appel du contrôleur (getConversations)
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getConversations(Long userId1, Long userId2) {
        return getConversation(userId1, userId2);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        if (!message.getExpediteur().getId().equals(userId) &&
            !message.getDestinataire().getId().equals(userId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce message");
        }
        messageRepository.delete(message);
    }
}