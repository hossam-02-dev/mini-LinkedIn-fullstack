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

@Table(name = "messages")
public class MessageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String contenu;
	
	@Column(nullable = false)
	private Boolean lu;
	
	@Column(nullable = false)
	private LocalDateTime dateEnvoi;
	
	@ManyToOne
	@JoinColumn(name = "expediteur_id")
	private UserEntity expediteur;
	
	@ManyToOne
	@JoinColumn(name = "destinataire_id")
	private UserEntity destinataire;

}
