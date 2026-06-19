package com.example.miniLinkedin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.ProjetEntity;

@Repository
public interface ProjetRepository extends JpaRepository<ProjetEntity, Long> {


    List<ProjetEntity> findByUserId(Long userId);

   
    List<ProjetEntity> findByTitreContainingIgnoreCase(String titre);

   
    List<ProjetEntity> findByUserIdOrderByDateCreationDesc(Long userId);
	
}
