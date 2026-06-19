package com.example.miniLinkedin.dtos;


import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class MessageResponseDto {
	
  private Long id;
	
	private String contenu;
	
	private Boolean lu;
	
   private Long expediteurId ;
   
   private Long destinataireId ;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime dateEnvoi;
}
