package com.example.miniLinkedin.dtos;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class LikeRequestDto {
	
	@NotNull(message = "l'utilisateur est obligatoire")
	private Long userId;
	
	@NotNull(message = "la publication est obligatoire")
	private Long publicationId;
	
	
	

}
