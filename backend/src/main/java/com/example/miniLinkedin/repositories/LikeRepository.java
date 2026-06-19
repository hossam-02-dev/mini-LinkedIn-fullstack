package com.example.miniLinkedin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.LikeEntity;

@Repository

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
	
	  boolean existsByUserIdAndPublicationId(Long userId, Long publicationId);
	  
	  Optional<LikeEntity> findByUserIdAndPublicationId(Long userId, Long publicationId);
	  
	  long countByPublicationId(Long publicationId);
	  
	  List<LikeEntity> findByPublicationId(Long publicationId);
	  
	   void deleteByPublicationId(Long publicationId);

}
