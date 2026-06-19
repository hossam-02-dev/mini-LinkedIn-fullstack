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

import com.example.miniLinkedin.dtos.CompetenceRequestDto;
import com.example.miniLinkedin.dtos.CompetenceResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.CompetenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/competences")

@Tag(
	    name = "Compétences", 
	    description = "Gestion des aptitudes et compétences techniques des utilisateurs"
	)

public class CompetenceRestController {
	
private final CompetenceService competenceService;

@Operation(
        summary = "Lister mes compétences",
        description = "Récupère l'ensemble des compétences ajoutées au profil de l'utilisateur connecté.",
        
        responses = {
        		
            @ApiResponse(
            		
            	description = "Liste récupérée", responseCode = "200"
            	),
            
            @ApiResponse(
            		
            		description = "Non autorisé", responseCode = "401"
            		)
        }
    )

@GetMapping("/mes-competences")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<List<CompetenceResponseDto>> getCompetenceByUser() {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	return ResponseEntity.status(HttpStatus.OK).body(competenceService.getCompetencesByUserId(userId));
	
}
@Operation(
        summary = "Ajouter une compétence",
        description = "Permet d'ajouter une nouvelle compétence (ex: Java, React, Gestion de projet) au profil.",
        responses = {
        		
            @ApiResponse(
            		
            		description = "Compétence créée", responseCode = "201"
            		
            		),
            @ApiResponse(
            		
            		description = "Données invalides", responseCode = "400"
            		)
        }
    )

@PostMapping
@PreAuthorize("isAuthenticated()")

public ResponseEntity<CompetenceResponseDto> createOne(@Valid @RequestBody  CompetenceRequestDto competenceRequestDto ) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	CompetenceResponseDto created = competenceService.addCompetence(userId, competenceRequestDto);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(created);
	
} 

@Operation(
        summary = "Modifier une compétence",
        description = "Met à jour le nom ou le niveau d'une compétence existante appartenant à l'utilisateur.",
        
        responses = {
        		
            @ApiResponse(
            		description = "Compétence mise à jour",
            		
            		responseCode = "200"
            		),
            
            @ApiResponse(
            		description = "Accès refusé ou compétence introuvable",
            		responseCode = "403"
            		
            		)
        }
    )

@PutMapping("/{competenceId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<CompetenceResponseDto> updateOne(@PathVariable Long competenceId ,  
	@Valid @RequestBody	CompetenceRequestDto competenceRequestDto) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
CompetenceResponseDto updated = competenceService.updateCompetence(competenceId, userId, competenceRequestDto);

return ResponseEntity.status(HttpStatus.OK).body(updated);
	
	
}

@Operation(
		
        summary = "Supprimer une compétence",
        
        description = "Retire définitivement une compétence du profil de l'utilisateur.",
        
        responses = {
            @ApiResponse(
            		
            		description = "Compétence supprimée", responseCode = "204"
            		
            		),
            @ApiResponse(
            		
            		description = "Action non autorisée",
            		
            		responseCode = "403"
            		)
        }
    )

@DeleteMapping("/{competenceId}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<Void> deleteOne(@PathVariable Long competenceId ) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	competenceService.deleteCompetence(competenceId, userId);
	
	return ResponseEntity.noContent().build();
	
}
	

}
