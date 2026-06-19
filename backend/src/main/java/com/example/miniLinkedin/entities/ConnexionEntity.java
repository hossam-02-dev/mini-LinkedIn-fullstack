package com.example.miniLinkedin.entities;

import java.time.LocalDateTime;

import com.example.miniLinkedin.enums.StatutConnexion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
@Table(name = "connexions")
public class ConnexionEntity {
@Id 
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
@Enumerated(EnumType.STRING)
private StatutConnexion statut;

@Column(nullable = false)
private LocalDateTime dateEnvoi;


private LocalDateTime dateReponse;

@ManyToOne
@JoinColumn(name = "demandeur_id")
private UserEntity demandeur;

@ManyToOne
@JoinColumn(name = "destinataire_id")
private UserEntity destinataire;

}
