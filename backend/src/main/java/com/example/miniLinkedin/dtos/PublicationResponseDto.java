package com.example.miniLinkedin.dtos;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class PublicationResponseDto {
    private Long id;
    private String contenu;
    private Long auteurId;
    private String imageUrl;
    private String nomAuteur;      // ← AJOUTER
    private String photoProfil;    // ← AJOUTER
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datePublication;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateMaj;
}