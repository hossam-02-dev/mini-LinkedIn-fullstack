package com.example.miniLinkedin.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

@Table(name = "profils")
public class ProfilEntity {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String name;

@Column(nullable = false)
private String ville;

@Column(nullable = false)
private String etablissement;

private String bio;
//LTE7T
@Column(nullable = true)
private LocalDate dateNaissance;


private LocalDateTime dateMaj;

@Column(nullable = false)
private String photoUrl;


@OneToOne(mappedBy = "profile")
private UserEntity user;

private String siteWeb;

@OneToMany(mappedBy = "profil")
private List<FormationEntity> formations;




}
