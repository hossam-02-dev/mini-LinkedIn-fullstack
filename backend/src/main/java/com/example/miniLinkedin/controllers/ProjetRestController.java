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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.miniLinkedin.dtos.ProjetRequestDto;
import com.example.miniLinkedin.dtos.ProjetResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.ProjetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projets")

@Tag(
	    name = " Projets & Réalisations", 
	    
	    description = "Gestion du portfolio technique : Publication, modification et recherche de projets académiques ou personnels"
	)

public class ProjetRestController {
	private final ProjetService projetService;
	
	@Operation(
	        summary = "Lister tous les projets",
	        
	        description = "Récupère l'intégralité des projets publiés sur la plateforme (Flux public).",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	           description = "Liste récupérée avec succès",
	           
	           responseCode = "200"
	           
	            		)
	        }
	    )
	
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<ProjetResponseDto>> getProjets() {
		
	return ResponseEntity.status(HttpStatus.OK).body(projetService.getAllProjets());
		
	}
	
	@Operation(
			
	        summary = "Rechercher des projets par titre",
	        
	        description = "Filtre les projets dont le titre correspond au mot-clé fourni.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	           description = "Résultats de recherche",
	           
	           responseCode = "200"
	           
	            		)
	        }
	    )
	
	@GetMapping("/search")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<ProjetResponseDto>> getProjetsByTitle(@RequestParam  String titre) {
		
	return ResponseEntity.status(HttpStatus.OK).body(projetService.getProjectsByTitle(titre));
		
	}
	
	@Operation(
			
	        summary = "Voir mes projets",
	        
	        description = "Récupère uniquement les projets publiés par l'utilisateur actuellement connecté.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Liste personnelle récupérée",
	            
	            responseCode = "200"
	            
	            		)
	        }
	    )
	
	@GetMapping("/mes-projets")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<List<ProjetResponseDto>> getProjetsByUser() {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
	return ResponseEntity.status(HttpStatus.OK).body(projetService.getProjetsByUserId(userId))	;
		
	}
	
	@Operation(
			
	        summary = "Détails d'un projet",
	        
	        description = "Récupère les informations complètes d'un projet spécifique via son identifiant.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            		description = "Projet trouvé",
	            		
	            		responseCode = "200"
	            		),
	            
	            @ApiResponse(
	            		
	            description = "Projet introuvable", 
	            
	            responseCode = "404"
	            
	            		)
	        }
	    )
	
	@GetMapping("/{projetId}")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<ProjetResponseDto> getProjet(@PathVariable Long projetId) {
		
	return ResponseEntity.status(HttpStatus.OK).body(projetService.getProjetById(projetId));
		
		
	}
	
	@Operation(
			
	        summary = "Publier un nouveau projet",
	        
	        description = "Permet aux Étudiants, Enseignants ou Chercheurs de soumettre une nouvelle réalisation au réseau.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Projet publié avec succès", 
	            
	            responseCode = "201"
	            ),
	            @ApiResponse(
	            		
	            	description = "Données invalides",
	            	
	            	responseCode = "400"
	            	
	            		),
	            
	            @ApiResponse(
	            		
	            	description = "Accès refusé",
	            	
	            	responseCode = "403"
	            	
	            		)
	        }
	    )
	@PostMapping
	@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
	
	public ResponseEntity<ProjetResponseDto> publierOne( @Valid @RequestBody ProjetRequestDto dto) {
		
		Long auteurId = SecurityUtils.getCurrentUserId();
		
		 ProjetResponseDto published = projetService.publierProjet(auteurId, dto);
		 
		 return ResponseEntity.status(HttpStatus.CREATED).body(published);
	}
	
	@Operation(
			
	        summary = "Modifier un projet",
	        
	        description = "Met à jour les détails d'un projet existant. Seul l'auteur peut modifier sa publication.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            		description = "Mise à jour réussie",
	            		
	            		responseCode = "200"
	            		
	            		),
	            @ApiResponse(
	            		
	            description = "Action non autorisée", 
	            
	            responseCode = "403"
	            
	            		),
	            @ApiResponse(
	            		
	            		description = "Projet non trouvé",
	            		
	            		responseCode = "404"
	            		
	            		)
	        }
	    )
	
@PutMapping("/{projetId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<ProjetResponseDto > updateOne(@PathVariable Long projetId , 
		
		@Valid @RequestBody ProjetRequestDto dto) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	ProjetResponseDto updated = projetService.updateProjet(projetId, userId, dto);
	
	return ResponseEntity.status(HttpStatus.OK).body(updated);
}
	
	@Operation(
			
	        summary = "Supprimer un projet",
	        
	        description = "Retire définitivement un projet du portfolio. Seul l'auteur du projet a ce droit.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Projet supprimé", 
	            
	            responseCode = "204"
	            
	            		),
	            @ApiResponse(
	            		
	            		description = "Action interdite", 
	            		
	            		responseCode = "403"
	            		
	            		)
	        }
	    )

@DeleteMapping("/{projetId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<Void> deleteOne(@PathVariable  Long projetId ) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
projetService.deleteProjet(projetId, userId);

return ResponseEntity.noContent().build();
	
}
}
