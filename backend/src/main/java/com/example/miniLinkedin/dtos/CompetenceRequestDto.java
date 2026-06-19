package com.example.miniLinkedin.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class CompetenceRequestDto {
	
	@NotBlank(message = "Le nom de la compétence est obligatoire")
	private String nom;
	
	@NotBlank(message = "Le niveau de la compétence est obligatoire")
	private String niveau;
	


}
