package com.example.miniLinkedin.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.miniLinkedin.dtos.ConnexionResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.ConnexionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/connexions")

@Tag(
	    name = " Réseau & Connexions", 
	    description = "Gestion des relations professionnelles : Demandes de connexion, acceptations et réseau d'amis"
	)

public class ConnexionRestController {
	
	private final ConnexionService connexionService;
	
	@Operation(
	        summary = "Envoyer une demande de connexion",
	        
	        description = "Initie une demande de mise en relation avec un autre utilisateur.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Demande envoyée", 
	            
	            responseCode = "201"
	            ),
	            
	            @ApiResponse(
	            		
	            	description = "Utilisateur non trouvé ou déjà connecté",
	            	
	            	responseCode = "400"
	            	)
	        }
	    )
	
	@PostMapping("/{destinataireId}")
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	
	public ResponseEntity<ConnexionResponseDto> sendDemande( @PathVariable Long destinataireId) {
		
		Long demandeurId = SecurityUtils.getCurrentUserId();
		
	ConnexionResponseDto sended = connexionService.envoyerDemande(demandeurId, destinataireId);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(sended);
	}
	
	
	@GetMapping("/statut/{userId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Map<String, Object>> getConnectionStatus(@PathVariable Long userId) {
	    Long currentUserId = SecurityUtils.getCurrentUserId();
	    String status = connexionService.getConnectionStatus(currentUserId, userId);
	    Map<String, Object> response = new HashMap<>();
	    response.put("status", status);
	    Long connexionId = connexionService.getConnexionId(currentUserId, userId);
	    if (connexionId != null) {
	        response.put("connexionId", connexionId);
	    }
	    return ResponseEntity.ok(response);
	}
	
	
	
	
	@Operation(
	        summary = "Accepter une demande",
	        
	        description = "Approuve une demande de connexion reçue pour l'ajouter à son réseau professionnel.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            		description = "Connexion acceptée",
	            		
	            		responseCode = "200"
	            		
	            	),
	            
	            @ApiResponse(
	            		
	            		description = "Demande introuvable ou action interdite",
	            		
	            		responseCode = "403"
	            		
	            		)
	        }
	        )
	
	@PutMapping("/{connexionId}/accepter")
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	
	public ResponseEntity<ConnexionResponseDto> acceptConnexion(@PathVariable  Long connexionId) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		ConnexionResponseDto accepted = connexionService.accepterConnexion(connexionId, userId);
		
		return ResponseEntity.status(HttpStatus.OK).body(accepted);
	}
	
	@Operation(
	        summary = "Refuser une demande",
	        
	        description = "Décline une invitation de connexion reçue.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            		description = "Demande refusée", 
	            		
	            		responseCode = "200"
	            		),
	            
	            @ApiResponse(
	            		
	            		description = "Action non autorisée",
	            		responseCode = "403"
	            		)
	        }
	    )
	
	@PutMapping("/{connexionId}/refuser")
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	
	public ResponseEntity<ConnexionResponseDto> refuseDemande(@PathVariable   Long connexionId) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		ConnexionResponseDto refused = connexionService.refuserDemande(connexionId, userId);
		
		return ResponseEntity.status(HttpStatus.OK).body(refused);
	}
	
	@Operation(
	        summary = "Lister mes relations",
	        
	        description = "Récupère la liste de tous les utilisateurs avec qui la connexion est confirmée (Amis).",
	        
	        responses = {
	        		
	            @ApiResponse(
	            	description = "Liste des relations récupérée", 
	            	
	            	responseCode = "200")
	        }
	    )
	
	
	@GetMapping("/acceptees")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<ConnexionResponseDto>> getConnexionAccepted() {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		return ResponseEntity.status(HttpStatus.OK).body(connexionService.getConnexionsAcceptees(userId));
	}
	
	@Operation(
	        summary = "Voir les invitations reçues",
	        
	        description = "Affiche la liste des demandes de connexion en attente de validation par l'utilisateur.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Liste des demandes récupérée",
	            
	            responseCode = "200"
	            )
	        }
	    )
	
	
	
	@GetMapping("/demandes-recues")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<ConnexionResponseDto>> getDemandes() {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		return ResponseEntity.status(HttpStatus.OK).body(connexionService.getDemandesRecues(userId));
		
	}
	
	
	@Operation(
	        summary = "Annuler ou supprimer une connexion",
	        
	        description = "Annule une demande envoyée par erreur ou supprime une relation existante du réseau.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            	description = "Action réussie (aucun contenu)",
	            	
	            	responseCode = "204"
	            	),
	            @ApiResponse(
	            		
	            description = "Connexion non trouvée",
	            
	            responseCode = "404"
	            )
	        }
	    )
	
	@DeleteMapping("/{connexionId}/annuler")
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	
	public ResponseEntity<Void> annulerDemande( @PathVariable Long connexionId) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		connexionService.annulerDemande(connexionId, userId);
		
		return ResponseEntity.noContent().build();
		
	}
	
	@Operation(
	        summary = "Supprimer un ami",
	        description = "Supprime définitivement une relation existante (un ami) du réseau.",
	        responses = {
	            @ApiResponse(description = "Action réussie (aucun contenu)", responseCode = "204"),
	            @ApiResponse(description = "Connexion non trouvée", responseCode = "404")
	        }
	)
	@DeleteMapping("/{connexionId}/supprimer")
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	public ResponseEntity<Void> supprimerConnexion(@PathVariable Long connexionId) {
	    
	    Long userId = SecurityUtils.getCurrentUserId();
	    
	    connexionService.supprimerConnexion(connexionId, userId);
	    
	    return ResponseEntity.noContent().build();
	}

}
