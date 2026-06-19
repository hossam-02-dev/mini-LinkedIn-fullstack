package com.example.miniLinkedin.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class PublicationRequestDto {
	
	@NotBlank(message = "le nom du contenu est obligatoire")
	private String contenu;
	
	@NotBlank(message = "l'image d'url est obligatoire")
	private String imageUrl;
	
	

}
