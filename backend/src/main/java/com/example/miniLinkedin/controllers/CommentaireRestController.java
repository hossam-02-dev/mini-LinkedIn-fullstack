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
import com.example.miniLinkedin.dtos.CommentaireRequestDto;
import com.example.miniLinkedin.dtos.CommentaireResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.CommentaireService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commentaires")

@Tag(
	name = "Commentaires",
	description = "Gestion des interactions : Création, modification et suppression des commentaires sur les publications "
		)

public class CommentaireRestController {
	
private final CommentaireService commentaireService;

@Operation(
        summary = "Récupérer mes commentaires",
        
        description = "Récupère la liste de tous les commentaires postés par l'utilisateur actuellement authentifié.",
        
        responses = {
        		
            @ApiResponse(
            	description = "Liste récupérée avec succès", responseCode = "200"
            	),
            
            @ApiResponse(
            		
            		description = "Non authentifié", responseCode = "401"
            		)
        }
    )


@GetMapping("/mes-commentaires")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<List<CommentaireResponseDto>> getCommentairesByAuteur()  {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
return ResponseEntity.status(HttpStatus.OK).body(commentaireService.getCommentairesByAuteur(userId));

}
@Operation(
        summary = "Récupérer les commentaires d'une publication",
        
        description = "Affiche tous les commentaires liés à une publication spécifique via son ID.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Commentaires trouvés", responseCode = "200"
            ),
            
            @ApiResponse(
            		
           description = "Publication non trouvée", responseCode = "404"
           
            		)
        }
    )

@GetMapping("/{publicationId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<List<CommentaireResponseDto>> getCommentairesByPublication(@PathVariable Long publicationId) {
	
return ResponseEntity.status(HttpStatus.OK).body(commentaireService.getCommentairesByPublication(publicationId));
	
}

@Operation(
        summary = "Ajouter un commentaire",
        
        description = "Permet aux Étudiants, Enseignants ou Chercheurs de commenter une publication.",
        
        responses = {
        		
            @ApiResponse(
            		
           description = "Commentaire créé", responseCode = "201"
           ),
            @ApiResponse(
            		
            description = "Données invalides ou accès refusé", responseCode = "403"
            
            		)
        }
    )

@PostMapping("/publication/{publicationId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<CommentaireResponseDto> createOne(@PathVariable Long publicationId , 
		@Valid @RequestBody CommentaireRequestDto dto ) {
	
	Long auteurId = SecurityUtils.getCurrentUserId();
	
	CommentaireResponseDto created = commentaireService.createCommentaire(publicationId, auteurId , dto);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(created);
}

@Operation(
        summary = "Modifier un commentaire",
        
        description = "Met à jour le contenu d'un commentaire existant. Seul l'auteur peut modifier son commentaire.",
        
        responses = {
            @ApiResponse(
            		
       description = "Commentaire mis à jour", responseCode = "200"
       ),
            @ApiResponse(
           description = "Interdit (non auteur)", responseCode = "403"
           ),
            @ApiResponse(
           description = "Commentaire non trouvé", responseCode = "404"
           
            		)
        }
    )

@PutMapping("/{commentaireId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<CommentaireResponseDto> updateOne(@PathVariable Long commentaireId , 
		
	@Valid @RequestBody CommentaireRequestDto dto ) {
	
	Long auteurId = SecurityUtils.getCurrentUserId();
	
	CommentaireResponseDto updated = commentaireService.updateCommentaire(commentaireId, auteurId,  dto);
	
	return ResponseEntity.status(HttpStatus.OK).body(updated);
}
@Operation(
        summary = "Supprimer un commentaire",
        description = "Supprime définitivement un commentaire. Nécessite d'être l'auteur du commentaire.",
        
        responses = {
            @ApiResponse(
            description = "Commentaire supprimé", responseCode = "204"
            ),
            @ApiResponse(
            description = "Action interdite", responseCode = "403"
            )
        }
    )

@DeleteMapping("/{commentaireId}")
@PreAuthorize("hasAnyRole('ETUDIANT','PROFESSEUR','CHERCHEUR')")

public ResponseEntity<Void> deleteOne(@PathVariable  Long commentaireId ) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	commentaireService.deleteCommentaire(commentaireId, userId);
	
	return ResponseEntity.noContent().build();
	
	
}

}
