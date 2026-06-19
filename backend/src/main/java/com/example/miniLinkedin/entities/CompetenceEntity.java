package com.example.miniLinkedin.entities;

import com.example.miniLinkedin.enums.Niveau;

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
@Table(name = "competences")
public class CompetenceEntity {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
	
 @Column(nullable = false)
private String nom;
 
 @ManyToOne
 @JoinColumn(name = "user_id")
 private UserEntity user;
 
 @Column(nullable = false)
@Enumerated(EnumType.STRING)
private  Niveau niveau;
 
}
