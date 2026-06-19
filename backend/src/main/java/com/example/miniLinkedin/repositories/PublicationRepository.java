package com.example.miniLinkedin.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.PublicationEntity;
@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, Long> {
	
	   List<PublicationEntity> findByAuteurId(Long auteurId);

	    
	    List<PublicationEntity> findAllByOrderByDatePublicationDesc();

	
	    List<PublicationEntity> findByAuteurIdOrderByDatePublicationDesc(Long auteurId);

}
