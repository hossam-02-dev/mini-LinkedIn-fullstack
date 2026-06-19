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

import com.example.miniLinkedin.dtos.FormationRequestDto;
import com.example.miniLinkedin.dtos.FormationResponseDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/formations")

@Tag(
	    name = "Formations", 
	    description = "Gestion du parcours académique : Ajout, modification et suppression des diplômes et certifications"
	)

public class FormationRestController {
	
	private  final FormationService formationService;
	
	@Operation(
	        summary = "Ajouter une formation",
	        
	        description = "Enregistre un nouveau diplôme ou une nouvelle certification dans le profil de l'utilisateur.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            		description = "Formation créée avec succès",
	            		
	            		responseCode = "201"
	            		
	            		),
	            
	            @ApiResponse(
	            		
	            description = "Données invalides ou profil non trouvé",
	            
	            responseCode = "400")
	        }
	    )
	
	@PostMapping("/profil/{profilId}")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<FormationResponseDto>createOne(@Valid @RequestBody FormationRequestDto dto ,@PathVariable Long profilId) {
		
	FormationResponseDto created = formationService.addFormation(profilId, dto)	;
	
	return ResponseEntity.status(HttpStatus.CREATED).body(created);
		
		
	}
	
	@Operation(
			
	        summary = "Lister les formations d'un profil",
	        
	        description = "Récupère tout le cursus académique lié à un profil spécifique via son ID.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            description = "Liste des formations récupérée",
	            
	            responseCode = "200"
	            
	            ),
	            
	            @ApiResponse(
	            		
	            description = "Profil introuvable",
	            
	            responseCode = "404"
	            
	            		)
	        }
	    )
	
	@GetMapping("/{profilId}")
	@PreAuthorize("isAuthenticated()")
	
	
	public ResponseEntity<List<FormationResponseDto>> getFormationByProfil(@PathVariable Long profilId ){
		
	return ResponseEntity.status(HttpStatus.OK).body(formationService.getFormationsByProfilId(profilId));
}
	
	
	@Operation(
	        summary = "Modifier une formation",
	        
	        description = "Met à jour les détails d'une formation existante. Seul le propriétaire du profil peut effectuer cette action.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            	
	            	description = "Mise à jour réussie",
	            	
	            	responseCode = "200"
	            	),
	            
	            @ApiResponse(
	            		
	            	description = "Accès refusé ou formation inexistante",
	            	
	            	responseCode = "403"
	            	)
	        }
	    )
	
	@PutMapping("/{formationId}")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<FormationResponseDto> updateOne(@PathVariable Long formationId  , 
		@Valid @RequestBody	FormationRequestDto dto) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		FormationResponseDto updated = formationService.updateFormation( formationId,userId, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body(updated);
		
	}
	
	@Operation(
	        summary = "Supprimer une formation",
	        
	        description = "Retire définitivement une formation du parcours académique de l'utilisateur.",
	        
	        responses = {
	        		
	            @ApiResponse(
	            		
	            	description = "Formation supprimée", 
	            	
	            	responseCode = "204"
	            	),
	            
	            @ApiResponse(
	            		
	            	description = "Action interdite",
	            	
	            	responseCode = "403"
	            	)
	        }
	    )
	
	@DeleteMapping("/{formationId}")
	@PreAuthorize("isAuthenticated()")
	
	public ResponseEntity<Void> deleteOne(@PathVariable Long formationId ) {
		
		Long userId = SecurityUtils.getCurrentUserId();
		
		formationService.deleteFormation(formationId , userId);
		
		return ResponseEntity.noContent().build();
	}
	
}
