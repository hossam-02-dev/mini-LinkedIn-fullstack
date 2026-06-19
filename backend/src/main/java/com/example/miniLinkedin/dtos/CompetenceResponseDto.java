package com.example.miniLinkedin.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class CompetenceResponseDto {
	private Long id;
	private Long userId;
	private String nom;
	private String niveau;
	

}
