package com.example.miniLinkedin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.FormationEntity;

@Repository
public interface FormationRepository extends JpaRepository<FormationEntity, Long> {
	
	  List<FormationEntity> findByProfilId(Long profilId);
	  
	  void deleteByProfilId(Long profilId);
}
