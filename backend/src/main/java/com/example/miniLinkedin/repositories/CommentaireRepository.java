package com.example.miniLinkedin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.CommentaireEntity;

@Repository
public interface CommentaireRepository extends JpaRepository<CommentaireEntity, Long> {
	
	List<CommentaireEntity> findByPublicationId(Long publicationId);
	
	List<CommentaireEntity> findByAuteurId(Long auteurId);

	
	long countByPublicationId(Long publicationId);
	
	void deleteByPublicationId(Long publicationId);

}
