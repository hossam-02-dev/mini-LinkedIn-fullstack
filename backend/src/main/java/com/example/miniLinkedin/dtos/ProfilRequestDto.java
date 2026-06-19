package com.example.miniLinkedin.dtos;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class ProfilRequestDto {
	
	@NotBlank(message = "Le nom est obligatoire")
		private String name;
	
	@NotBlank(message = "La ville est obligatoire")
	private String ville;
	
	@NotBlank(message = "Le nom d'établissement est obligatoire")
	private String etablissement;
	
	private String bio;
	

	private String siteWeb;
	
	
	private String photoUrl;
	
	
	@Past(message = "La date de naissance doit être dans le passé")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateNaissance;
	
	
}
