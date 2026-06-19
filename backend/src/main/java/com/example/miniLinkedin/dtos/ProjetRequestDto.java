package com.example.miniLinkedin.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class ProjetRequestDto {
	
	@NotBlank(message = "Le titre du projet est obligatoire")
	private String titre;
	
	@NotBlank(message = "la description du projet est obligatoire")
	private String description;
	
	
	@NotBlank(message = "les technologies utilisées dans le projet sont obligatoires")
	private String technologies;
	
	@NotBlank(message = "le lien github du projet est obligatoire")
	private String lienGithub;
	

	@NotBlank(message = "le lien de démonstration du projet est obligatoire")
	private String lienDemo;
	
	@NotBlank(message = "l'url de l'image du projet est obligatoire")
	private String imageUrl;

	
	
	

}
