package com.example.miniLinkedin.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.miniLinkedin.dtos.ProfilRequestDto;
import com.example.miniLinkedin.dtos.ProfilResponseDto;
import com.example.miniLinkedin.dtos.ProfileStatsDto;
import com.example.miniLinkedin.security.SecurityUtils;
import com.example.miniLinkedin.services.ProfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profils")

@Tag(
	    name = "Profils Utilisateurs",
	    
	    description = "Gestion des informations d'identité : Création, consultation et mise à jour du profil professionnel"
	)

public class ProfilRestController {
	
private final ProfilService profilService;


//1. Endpoint pour récupérer les stats
@GetMapping("/me/stats")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ProfileStatsDto> getMyStats() {
 Long userId = SecurityUtils.getCurrentUserId();
 return ResponseEntity.ok(profilService.getMyProfileStats(userId));
}

//2. Mettre à jour l'Endpoint qui récupère le profil D'UN AUTRE utilisateur
//pour déclencher l'enregistrement de la vue (si vous avez un GET /api/profils/{id})
@GetMapping("/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ProfilResponseDto> getProfilById(@PathVariable Long id) {
 Long currentUserId = SecurityUtils.getCurrentUserId();
 
 // Enregistrer la vue en arrière-plan
 profilService.recordProfileView(id, currentUserId);
 
 return ResponseEntity.ok(profilService.getProfilByUserId(id)); // Assurez-vous d'avoir cette méthode
}








@Operation(
        summary = "Récupérer mon profil",
        
        description = "Récupère les informations détaillées du profil de l'utilisateur actuellement authentifié.",
        
        responses = {
        		
            @ApiResponse(
            		
            	description = "Profil récupéré avec succès", 
            	
            	responseCode = "200"
            	),
            
            @ApiResponse(
            		
            description = "Profil non trouvé pour cet utilisateur", 
            
            responseCode = "404"
            
            		)
        }
    )


@GetMapping
@PreAuthorize("isAuthenticated()")

public ResponseEntity<ProfilResponseDto> getProfilByUser () {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
return ResponseEntity.status(HttpStatus.OK).body(profilService.getProfilByUserId(userId));
	
}

@Operation(
        summary = "Créer un profil",
        
        description = "Initialise le profil professionnel de l'utilisateur (Bio, titre, etc.) juste après l'inscription.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Profil créé", 
            
            responseCode = "201"
            
            		),
            @ApiResponse(
            		
            description = "Données invalides ou profil déjà existant",
            
            responseCode = "400"
            
            		)
        }
    )

@PostMapping
@PreAuthorize("isAuthenticated()")

public ResponseEntity<ProfilResponseDto> createOne ( @Valid @RequestBody  ProfilRequestDto dto ) {
	
	Long userId = SecurityUtils.getCurrentUserId();
	
	ProfilResponseDto created = profilService.createProfil(userId, dto);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(created);
}

@Operation(
		
        summary = "Mettre à jour le profil",
        
        description = "Modifie les informations générales du profil (Titre professionnel, résumé, localisation).",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Profil mis à jour",
            
            responseCode = "200"
            
            		),
            
            @ApiResponse(
            		
            	description = "Accès refusé ou profil introuvable",
            	
            	responseCode = "403"
            	
            		)
        }
    )

@PutMapping("/{profilId}")
@PreAuthorize("isAuthenticated()")


public ResponseEntity<ProfilResponseDto> updateOne(@Valid @RequestBody ProfilRequestDto dto ,@PathVariable Long profilId) {
	
	ProfilResponseDto updated = profilService.updateProfil(profilId, dto);
	
	return ResponseEntity.status(HttpStatus.OK).body(updated);
}

@Operation(
		
        summary = "Mettre à jour la photo de profil",
        
        description = "Enregistre l'URL de la nouvelle photo de profil de l'utilisateur.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Photo mise à jour",
            
            responseCode = "200"
            
            		),
            @ApiResponse(
            		
            description = "Format d'URL invalide",
            
            responseCode = "400"
            
            		)
        }
    )

@PutMapping("/{profilId}/photo")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<ProfilResponseDto> uploadFile(@PathVariable Long profilId ,@RequestParam String photoUrl) {
	
	ProfilResponseDto uploaded = profilService.uploadPhoto(profilId, photoUrl);
	
	return ResponseEntity.status(HttpStatus.OK).body(uploaded);
	
}
}

