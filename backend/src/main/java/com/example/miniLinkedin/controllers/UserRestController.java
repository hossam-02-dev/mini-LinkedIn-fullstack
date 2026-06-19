package com.example.miniLinkedin.controllers;

import java.util.List;
import java.util.stream.Collectors;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.example.miniLinkedin.dtos.UserRequestDto;
import com.example.miniLinkedin.dtos.UserResponseDto;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.mapping.UserMapper;
import com.example.miniLinkedin.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

@Tag(
	    name = "Gestion des Utilisateurs (Admin)", 
	    
	    description = "Administration système : Gestion des comptes, création manuelle et désactivation des utilisateurs"
	)

public class UserRestController {
	

private final UserService userService;
private final UserMapper userMapper;


@Operation(
		
        summary = "Lister tous les comptes",
        
        description = "Récupère la liste complète de tous les utilisateurs inscrits. Accès réservé aux administrateurs.",
        
        responses = {
        		
            @ApiResponse(
            		
            description = "Liste récupérée", 
            
            responseCode = "200"
            
            ),
            
            @ApiResponse(
            		
            description = "Accès interdit", 
            
            responseCode = "403"
            
            		)
        }
    )

@GetMapping
@PreAuthorize("hasRole('ADMIN')")

public ResponseEntity<List<UserResponseDto>> getUsers() {
	
return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
	
}




@GetMapping("/search")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String q) {
    List<UserEntity> users = userService.searchUsers(q);
    List<UserResponseDto> dtos = users.stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}

@Operation(
		
        summary = "Détails d'un compte",
        
        description = "Récupère les informations de base d'un utilisateur par son ID. Accessible par tout utilisateur authentifié.",
        
        responses = {
        		
            @ApiResponse(
            		
           description = "Utilisateur trouvé",
           
           responseCode = "200"
           
            		),
            
            @ApiResponse(
            		
            description = "Utilisateur introuvable", 
            
            responseCode = "404"
            
            		)
        }
    )

@GetMapping("/{id}")
@PreAuthorize("isAuthenticated()")

public ResponseEntity<UserResponseDto> getUser (@PathVariable Long id) {
	
	return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
	
	
}

@Operation(
		
        summary = "Créer un utilisateur (Admin)",
        
        description = "Permet à un administrateur de créer manuellement un compte utilisateur.",
        
        responses = {
        		
            @ApiResponse(
            		
            		description = "Compte créé",
            		
            		responseCode = "201"
            		
            		),
            
            @ApiResponse(
            		
            		description = "Données invalides",
            		
            		responseCode = "400"
            		)
        }
    )

@PostMapping
@PreAuthorize("hasRole('ADMIN')")


public ResponseEntity<UserResponseDto> createOne (@Valid @RequestBody UserRequestDto dto) {
	
	UserResponseDto created = userService.createUser(dto);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(created);
}

@Operation(
		
        summary = "Modifier un compte (Admin)",
        
        description = "Permet de modifier les informations système d'un utilisateur via son ID.",
        
        responses = {
        		
            @ApiResponse(
            		description = "Mise à jour réussie", 
            		
            		responseCode = "200"
            		
            		),
            
            @ApiResponse(
            		
            		description = "Utilisateur inexistant",
            		
            		responseCode = "404")
        }
    )
@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")


public ResponseEntity<UserResponseDto> updateOne(@Valid @RequestBody UserRequestDto dto , @PathVariable Long id) {
	
	UserResponseDto updated = userService.updateUser(id, dto);
	
	return ResponseEntity.status(HttpStatus.OK).body(updated);
}


@Operation(
		
        summary = "Désactiver un compte",
        
        description = "Action d'administration pour suspendre ou désactiver l'accès d'un utilisateur au réseau.",
        
        responses = {
        		
            @ApiResponse(
            		
            		description = "Utilisateur désactivé",
            		
            		responseCode = "204"
            		
            		),
            
            @ApiResponse(
            		
            description = "Action non autorisée",
            
            responseCode = "403"
            
            		)
        }
    )
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")

public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
	
	userService.DesactivateUser(id);
	
	return ResponseEntity.noContent().build();
	
}

}
