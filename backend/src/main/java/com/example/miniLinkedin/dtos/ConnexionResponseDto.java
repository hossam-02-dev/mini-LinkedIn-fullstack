package com.example.miniLinkedin.dtos;


import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class ConnexionResponseDto {
	
	private Long id;
	
	private String statutConnexion;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime dateEnvoi;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime dateReponse;
	
	private Long destinataireId;
	
	private Long demandeurId;
}
