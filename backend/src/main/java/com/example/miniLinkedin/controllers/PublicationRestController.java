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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.example.miniLinkedin.dtos.PublicationRequestDto;
import com.example.miniLinkedin.dtos.PublicationResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.PublicationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publications")

@Tag(
	    name = " Publications & Fil d'actualité", 
	    
	    description = "Gestion du contenu social : Création de posts, consultation du feed et gestion des publications individuelles"
	)

public class PublicationRestController {
	
private final PublicationService publicationService;

@Operation(
		
        summary = "Consulter une publication",
        
        description = "Récupère les détails d'une publication spécifique (contenu, date, auteur) via son ID.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Publication trouvée",
            
            responseCode = "200"
            
            		),
            @ApiResponse(
            		
            		description = "Publication inexistante", 
            		responseCode = "404"
            		
            		)
        }
    )

@GetMapping("/{publicationId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<PublicationResponseDto> getPublication( @PathVariable   Long publicationId) {
	
	return ResponseEntity.status(HttpStatus.OK).body(publicationService.getPublicationById(publicationId));
	
}

@Operation(
		
        summary = "Voir les publications d'un auteur",
        
        description = "Récupère la liste chronologique des posts publiés par un utilisateur spécifique.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Liste récupérée avec succès",
            
            responseCode = "200"
            
            		)
        }
    )
@GetMapping("/user/{auteurId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<List<PublicationResponseDto>> getPublicationsByAuteur( @PathVariable Long auteurId) {
	
	return ResponseEntity.status(HttpStatus.OK).body(publicationService.getPublicationsByAuteurId(auteurId));
	
}

@Operation(
		
        summary = "Récupérer le fil d'actualité (Feed)",
        
        description = "Affiche les dernières publications de la plateforme. Idéal pour la page d'accueil.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Flux d'actualité chargé",
            
            responseCode = "200"
            
            		)
        }
    )
@GetMapping("/feed")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<List<PublicationResponseDto>> getFeed() {
	
	return ResponseEntity.status(HttpStatus.OK).body(publicationService.getFeed());
}

@Operation(
		
        summary = "Créer un nouveau post",
        
        description = "Permet aux Étudiants, Enseignants ou Chercheurs de partager du contenu sur le réseau.",
        
        responses = {
        		
            @ApiResponse(
            		
           description = "Publication créée", 
           
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
@PreAuthorize("isAuthenticated()")

public ResponseEntity<PublicationResponseDto> createOne(@Valid @RequestBody PublicationRequestDto dto){
	
	Long auteurId = SecurityUtils.getCurrentUserId();
	
PublicationResponseDto created = publicationService.createPublication(auteurId, dto);

return ResponseEntity.status(HttpStatus.CREATED).body(created);
}


@Operation(
		
        summary = "Modifier un post",
        
        description = "Met à jour le contenu d'une publication existante. Seul l'auteur original peut modifier son post.",
        
        responses = {
        		
            @ApiResponse(
            		
            		description = "Mise à jour réussie", 
            		
            		responseCode = "200"
            		
            		),
            @ApiResponse(
            		
            		description = "Action interdite (non-auteur)",
            		
            		responseCode = "403"
            		
            		),
            
            @ApiResponse(
            	
            description = "Publication introuvable",
            
            responseCode = "404"
            )
        }
    )
@PutMapping("/{publicationId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<PublicationResponseDto> updateOne(@PathVariable Long publicationId
		
 , @Valid @RequestBody PublicationRequestDto dto) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	
	PublicationResponseDto updated = publicationService.updatePublication(publicationId, userId, dto);
	
	return ResponseEntity.status(HttpStatus.OK).body(updated);
	
	
}

@Operation(
		
        summary = "Supprimer un post",
        
        description = "Retire définitivement une publication du réseau. Nécessite d'être l'auteur du contenu.",
        
        responses = {
        		
            @ApiResponse(
            		description = "Publication supprimée", 
            		
            		responseCode = "204"
            		
            		),
            
            @ApiResponse(
            		
            description = "Action non autorisée", 
            
            responseCode = "403"
            )
        }
    )

@DeleteMapping("/{publicationId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<Void> deleteOne(@PathVariable Long publicationId ) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	publicationService.deletePublication(publicationId, userId);
	
	return ResponseEntity.noContent().build();
}

}
