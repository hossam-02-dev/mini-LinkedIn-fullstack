package com.example.miniLinkedin.dtos;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

 @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
 
public class ProfilResponseDto {
	 
	private Long id;
	private Long userId;
	private String nomComplet;
	private String name;
	private String ville;
	private String etablissement;
	private String bio;
	private String siteWeb;
	private String photoUrl;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateNaissance;
	



}
