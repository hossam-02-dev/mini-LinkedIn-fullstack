package com.example.miniLinkedin.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.miniLinkedin.dtos.NotificationResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class WebSocketNotificationService {
	
	 private final SimpMessagingTemplate messagingTemplate;
	 
	 public void envoyerNotification(Long userId, NotificationResponseDto notification) {
		 
	        messagingTemplate.convertAndSendToUser(
	        		
	            String.valueOf(userId),       // identifiant de l'utilisateur
	            "/queue/notifications",       // destination côté client
	            notification                  // payload — le DTO de la notification
	        );
	    }

}
