package com.example.miniLinkedin.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import com.example.miniLinkedin.dtos.NotificationResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")

@Tag(
	    name = "Notifications", 
	    
	    description = "Gestion des alertes utilisateur : Consultation, marquage comme lu et comptage des notifications non lues"
	)

public class NotificationRestController {
	
private final NotificationService notificationService;	

@Operation(
		
        summary = "Récupérer une notification",
        
        description = "Affiche les détails d'une notification spécifique via son ID.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Notification trouvée", 
            
            responseCode = "200"
            ),
            @ApiResponse(
            		
            description = "Notification inexistante", 
            
            responseCode = "404"
            
            		)
        }
    )


@GetMapping("/{id}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<NotificationResponseDto> getNotification(@PathVariable Long id) {
	
	return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotificationById(id));
	
}

@Operation(
		
        summary = "Lister les notifications d'un utilisateur",
        
        description = "Récupère l'historique complet des notifications reçues par un utilisateur spécifique.",
        
        responses = {
        		
            @ApiResponse(
            		
            		description = "Liste des notifications récupérée",
            		
            		responseCode = "200"
            		
            		)
        }
    )

@GetMapping("/user/{destinataireId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<List<NotificationResponseDto>>  getNotifsByDestinataire(@PathVariable Long destinataireId) {
	
	return ResponseEntity.status(HttpStatus.OK).body(notificationService
			.getNotificationsByDestinataireId(destinataireId));
}


@Operation(
		
        summary = "Marquer une notification comme lue",
        
        description = "Met à jour le statut d'une notification. Seul le destinataire peut marquer sa propre notification comme lue.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Statut mis à jour",
            
            responseCode = "200"
            
            ),
            
            @ApiResponse(
            		
            description = "Accès refusé", 
            
            responseCode = "403"
            
           )
        }
    )

@PutMapping("/{notificationId}/lu")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<NotificationResponseDto> markAsRead(@PathVariable	  Long notificationId) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	return ResponseEntity.status(HttpStatus.OK).body(notificationService.markNotificationAsRead(notificationId, userId));
}

@Operation(
		
        summary = "Supprimer une notification",
        
        description = "Supprime définitivement une notification de l'historique de l'utilisateur.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Notification supprimée", 
            
            responseCode = "204"
            
            		),
            @ApiResponse(
            		
            	description = "Action non autorisée",
            	
            	responseCode = "403"
            	
           )
        }
    )

@DeleteMapping("/{notificationId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<Void> deleteOne(@PathVariable Long notificationId  ){
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	notificationService.deleteNotification(notificationId, userId);
	
	return ResponseEntity.noContent().build();
}

@Operation(
		
        summary = "Compter les notifications non lues",
        
        description = "Retourne le nombre de notifications que l'utilisateur n'a pas encore consultées. Idéal pour l'affichage d'un badge sur l'interface.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Nombre récupéré avec succès", 
            
            responseCode = "200"
            
            		)
        }
    )

@GetMapping("/non-lues/count")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<Long> countUnreadNotifs() {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	return ResponseEntity.status(HttpStatus.OK).body(notificationService.countUnreadNotifications(userId));
	
}

}
