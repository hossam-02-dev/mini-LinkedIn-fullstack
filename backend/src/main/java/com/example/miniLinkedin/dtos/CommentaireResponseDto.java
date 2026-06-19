package com.example.miniLinkedin.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class CommentaireResponseDto {
private Long id;

private String texte;

@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime date;

private String nomAuteur;

private Long auteurId;

}
