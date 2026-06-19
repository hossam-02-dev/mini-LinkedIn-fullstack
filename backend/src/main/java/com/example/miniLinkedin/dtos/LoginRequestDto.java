package com.example.miniLinkedin.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class LoginRequestDto {
	
	@NotBlank(message = "L'email ne peut pas être vide")
	@Email
	private String email;
	
	@NotBlank(message = "Le mot de passe ne peut pas être vide")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
	private String password;

}
