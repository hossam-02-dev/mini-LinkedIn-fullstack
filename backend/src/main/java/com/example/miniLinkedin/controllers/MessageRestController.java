package com.example.miniLinkedin.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.miniLinkedin.dtos.MessageResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")

@Tag(
	    name = "Messagerie Privée", 
	    description = "Gestion des échanges directs : Envoi de messages, lecture de conversations et notifications de messages non lus"
	)

public class MessageRestController {
	
	private final MessageService messageService;
	
	@Operation(
			
	        summary = "Récupérer les messages non lus",
	        
	        description = "Liste tous les messages reçus par l'utilisateur connecté qui n'ont pas encore été marqués comme lus.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            	description = "Liste des messages non lus récupérée",
	            	
	            	responseCode = "200"
	            	),
	            
	            @ApiResponse(
	            		
	            description = "Utilisateur non authentifié",
	            
	            responseCode = "401"
	            )
	        }
	    )
	
	@GetMapping("/non-lus")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<MessageResponseDto>> getMessagesNonLus() {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		return ResponseEntity.status(HttpStatus.OK).body(messageService.getMessagesNonLus(userId));
	}
	
	
	
	@Operation(
			
	        summary = "Récupérer une conversation",
	        
	        description = "Affiche l'historique des échanges entre l'utilisateur connecté et un autre utilisateur spécifique.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Historique de conversation récupéré", 
	            
	            responseCode = "200"
	            
	            		),
	            @ApiResponse(
	            		
	            description = "Destinataire introuvable", 
	            
	            responseCode = "404"
	            )
	        }
	    )
	
	@GetMapping("/conversation/{userId2}")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<MessageResponseDto>> getConversations(@PathVariable Long userId2) {
		
		Long userId1 = SecurityUtils.getCurrentUserId();
		
		return ResponseEntity.status(HttpStatus.OK).body(messageService.getConversations(userId1, userId2));
	}
	
	@Operation(
			
	        summary = "Marquer un message comme lu",
	        
	        description = "Change le statut d'un message spécifique de 'non lu' à 'lu'. Seul le destinataire du message peut effectuer cette action.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Message marqué comme lu (aucun contenu)",
	            
	            responseCode = "204"
	            
	            		),
	            
	            @ApiResponse(
	            		
	            description = "Accès refusé ou message inexistant",
	            
	            responseCode = "403"
	            
	            		)
	        }
	    )
	
	@PutMapping("/{messageId}/lu")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		messageService.markMessageAsReadById(messageId, userId);
		
		return ResponseEntity.noContent().build();
		
	}
	
	
	@Operation(
			
	        summary = "Envoyer un message privé",
	        
	        description = "Permet d'envoyer un message textuel à un destinataire spécifique. Action réservée aux rôles académiques.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Message envoyé avec succès", 
	            
	            responseCode = "201"
	            
	            ),
	            @ApiResponse(
	            		description = "Contenu vide ou destinataire invalide",
	            		
	            		responseCode = "400"
	            		)
	        }
	    )
	
	@PostMapping("/{destinataireId}")
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	
	public ResponseEntity<MessageResponseDto> sendOne(@PathVariable Long destinataireId  
			,@RequestParam String contenu) {
		
		Long expediteurId = SecurityUtils.getCurrentUserId();
		
	MessageResponseDto	sended = messageService.sendMessage(destinataireId, expediteurId, contenu);
	return ResponseEntity.status(HttpStatus.CREATED).body(sended);
		
	}
	
	@Operation(
			
	        summary = "Supprimer un message",
	        
	        description = "Supprime un message de l'historique. Note : la suppression peut être unilatérale ou bilatérale selon votre logique service.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            	description = "Message supprimé avec succès",
	            	
	            	responseCode = "204"
	            	),
	            
	            @ApiResponse(
	            		
	            description = "Action non autorisée",
	            
	            responseCode = "403"
	            
	            		)
	        }
	    )
	
	@DeleteMapping("/{messageId}")
	@PreAuthorize("isAuthenticated()")

public ResponseEntity<Void> deleteOne( @PathVariable Long messageId ) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
	messageService.deleteMessage(messageId, userId);
	
	return ResponseEntity.noContent().build();
}
}
