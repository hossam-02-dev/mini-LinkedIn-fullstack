package com.example.miniLinkedin.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.example.miniLinkedin.dtos.LikeResponseDto;
// import com.example.miniLinkedin.security.SecurityUtils; // removed unused import
// import org.springframework.security.core.annotation.AuthenticationPrincipal; // removed unused import
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.LikeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@Tag(
	    name = "Likes & Interactions", 
	    
	    description = "Gestion des réactions sur les publications (Ajouter, supprimer et compter les likes)"
	)

public class LikeRestController {
	
private final LikeService likeService;	

@Operation(
        summary = "Liker une publication",
        
        description = "Permet à un utilisateur (Étudiant, Enseignant ou Chercheur) d'ajouter une mention 'J'aime' sur une publication spécifique.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Like ajouté avec succès",
            
            responseCode = "201"
            ),
            
            @ApiResponse(
            		
            		description = "Publication non trouvée ou déjà likée",
            		
            		responseCode = "400"
            		),
            
            @ApiResponse(
            		
            		description = "Accès refusé",
            		
            		responseCode = "403"
            		)
        }
    )

    @PostMapping("/publication/{publicationId}")
    @PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
    public ResponseEntity<LikeResponseDto> addOne(@PathVariable Long publicationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        LikeResponseDto added = likeService.addLike(publicationId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

@Operation(
        summary = "Compter les likes",
        
        description = "Récupère le nombre total de mentions 'J'aime' pour une publication donnée.",
        
        responses = {
        		
            @ApiResponse(
            		
            	description = "Nombre récupéré",
            	
            	responseCode = "200"
            	),
            
            @ApiResponse(
            		
            description = "Publication non trouvée",
            
            responseCode = "404"
            )
        }
    )
@GetMapping("/publication/{publicationId}/count")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<Long> countByPublications(@PathVariable Long publicationId) {
	
	long counted = likeService.countLikesByPublication(publicationId);
	
	return ResponseEntity.status(HttpStatus.OK).body(counted);
	
}

@Operation(
		
        summary = "Retirer un like",
        
        description = "Permet de supprimer sa mention 'J'aime' d'une publication.",
        
        responses = {
        		
            @ApiResponse(
            		
            	description = "Like retiré (aucun contenu)",
            	
            	responseCode = "204"
            	),
            @ApiResponse(
            		
            	description = "Action impossible ou non autorisée", 
            	
            	responseCode = "403"
            	)
        }
    )

    @DeleteMapping("/publication/{publicationId}")
    @PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")
    public ResponseEntity<Void> removeOne(@PathVariable Long publicationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        likeService.removeLike(publicationId, userId);
        return ResponseEntity.noContent().build();
    }

}
