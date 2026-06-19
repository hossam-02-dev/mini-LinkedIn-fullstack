package com.example.miniLinkedin.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Table(name = "formations")

public class FormationEntity {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String diplome;

@Column(nullable = false)
private String etablissement;

@Column(nullable = false)
private String domaine;

@Column(nullable = false)
private Boolean enCours;

@Column(nullable = false)
private LocalDate dateDebut;

@Column(nullable = false)
private LocalDate dateFin;
@ManyToOne
@JoinColumn(name = "profil_id")
private ProfilEntity profil;


}
