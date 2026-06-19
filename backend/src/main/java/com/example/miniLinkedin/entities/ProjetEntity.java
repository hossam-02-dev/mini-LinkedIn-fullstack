package com.example.miniLinkedin.entities;

import java.time.LocalDateTime;

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
@Table(name = "projets")
public class ProjetEntity {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String titre;

@Column(nullable = false)
private String description;

@Column(nullable = false)
private String technologies;

@Column(nullable = false)
private String lienGithub;

@Column(nullable = false)
private String lienDemo;

@Column(nullable = false)
private String imageUrl;

@Column(nullable = false)
private LocalDateTime dateCreation;

private LocalDateTime dateMaj;

@ManyToOne
@JoinColumn(name = "user_id")
private UserEntity user;

}
