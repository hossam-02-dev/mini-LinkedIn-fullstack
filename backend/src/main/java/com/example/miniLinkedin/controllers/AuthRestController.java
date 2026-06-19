package com.example.miniLinkedin.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniLinkedin.dtos.AuthResponseDto;
import com.example.miniLinkedin.dtos.LoginRequestDto;
import com.example.miniLinkedin.dtos.RefreshTokenRequestDto;
import com.example.miniLinkedin.dtos.RegisterRequestDto;
import com.example.miniLinkedin.dtos.UserResponseDto;
import com.example.miniLinkedin.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")

@Tag(
name = "Authentification",	
description = "Gestion du cycle de vie des utilisateurs :Inscription , Connexion , Validation des comptes"
		)


public class AuthRestController {
	
private final AuthService authService;

@Operation(
		
	description = "POST EndPoint pour l'inscription",
	summary = "Ce summary est pour l'inscription d'un utilisateur",
	responses = {
	@ApiResponse(
			description = "Utilisateur créé avec succès",
			responseCode = "200"
			
			),
	        @ApiResponse(
	       description = "Identifiants invalides",
	       responseCode = "401"
	       
	    )
	
	}
		
	)




@PostMapping("/register")

public ResponseEntity<UserResponseDto> register(@Valid @RequestBody   RegisterRequestDto dto) {
	
	UserResponseDto registred = authService.inscrire(dto);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(registred);
	
}


@Operation(
	    summary = "Activer un compte utilisateur",
	    
	    description = "Valide l'inscription via le token reçu par email.",
	    
	    responses = {
	        @ApiResponse(
	        description = "Compte activé avec succès", 
	        responseCode = "204"
	        ),
	        
	        @ApiResponse(
	        		description = "Token invalide ou expiré",
	        		responseCode = "400"
	        		)
	    }
	    )


@GetMapping("/activate")

public ResponseEntity<Void> activer(@RequestParam  String token) {
	
	authService.activer(token);
	
	return ResponseEntity.noContent().build();
}

@Operation(
	description = "Post EndPoint pour la connexion d'un utilisateur  ",
	summary = "Ce summary est pour la connexion d'un utilsateur",
	
	responses = {
			
		@ApiResponse(
				
			description = "Success",
			responseCode = "200"
				
				)	,
		@ApiResponse(
			description = "Bad Request",
			responseCode = "400"
				
				)
	}
		
		)

@PostMapping("/login")

public ResponseEntity<AuthResponseDto> login (@Valid @RequestBody  LoginRequestDto dto)  {
	
	AuthResponseDto  logged = authService.connecter(dto);
	return ResponseEntity.status(HttpStatus.OK).body(logged);
}


@Operation(
        summary = "Rafraîchir le token",
        description = "Génère un nouvel Access Token à partir d'un Refresh Token valide.",
        responses = {
            @ApiResponse(
            description = "Nouveaux tokens générés", 
            responseCode = "200"
            ),
            @ApiResponse(
           description = "Refresh Token invalide ou expiré",
           responseCode = "401"
           
            		)
        }
    )


@PostMapping("/refresh-token")

public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto dto) {
	
	return ResponseEntity.ok(authService.rafraichirToken(dto));
	}

}
