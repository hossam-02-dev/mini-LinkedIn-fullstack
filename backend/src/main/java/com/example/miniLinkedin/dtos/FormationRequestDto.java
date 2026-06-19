package com.example.miniLinkedin.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class FormationRequestDto {
	
@NotBlank(message = "Le diplome est obligatoire")
	private String diplome;

@NotBlank(message = "L'etablissement est obligatoire")
	private String etablissement;

@NotBlank(message = "Le domaine est obligatoire")
	private String domaine;

@NotNull(message = "Le champ enCours est obligatoire")
	private Boolean enCours;

@PastOrPresent(message = "La date de début doit être dans le passé ou aujourd'hui")
	private LocalDate dateDebut;

@PastOrPresent(message = "La date de fin doit être dans le passé ou aujourd'hui")
	private LocalDate dateFin;
	
}
