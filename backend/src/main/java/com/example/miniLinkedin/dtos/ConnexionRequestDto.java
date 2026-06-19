package com.example.miniLinkedin.dtos;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class ConnexionRequestDto {
	
	@NotNull(message = "L'identifiant du destinataire est obligatoire")
	private Long destinataireId;

}
