package com.example.miniLinkedin.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class CommentaireRequestDto {
	
@NotNull(message = "L'identifiant de l'auteur ne peut pas être nul")
	private Long auteurId;

	@NotNull(message = "L'identifiant de la publication ne peut pas être nul")
	private Long publicationId;
	
	@NotBlank(message = "Le texte du commentaire ne peut pas être vide")
	private String texte;
	

	

}
