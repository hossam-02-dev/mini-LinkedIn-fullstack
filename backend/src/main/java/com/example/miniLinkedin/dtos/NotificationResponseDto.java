package com.example.miniLinkedin.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class NotificationResponseDto {
	private Long id;
	private String contenu;
	private Boolean lu;
	private String type;
	private LocalDateTime date;
	private Long destinataireId;
	private Long declencheurId;

}
