package com.example.miniLinkedin.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

@Table(name = "publications")
public class PublicationEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;
	
	@ManyToOne
	@JoinColumn(name = "auteur_id")
	private UserEntity auteur;
	
@Column(nullable = false)
private String contenu;

@Column(nullable = false)
private String imageUrl;

@Column(nullable = false)
private LocalDateTime datePublication;


private LocalDateTime dateMaj;

@OneToMany(mappedBy = "publication")
private List<CommentaireEntity> commentaires;

@OneToMany(mappedBy = "publication")
private List<LikeEntity> likes;

}
