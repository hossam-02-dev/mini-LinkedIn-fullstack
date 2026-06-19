package com.example.miniLinkedin.dtos;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class FormationResponseDto {
	
private Long id;
private Long profilId;
private String diplome;
private String etablissement;
private String domaine;
private Boolean enCours;

@JsonFormat(pattern = "yyyy-MM-dd")
private LocalDate dateDebut;

@JsonFormat(pattern = "yyyy-MM-dd")
private LocalDate dateFin;

}
