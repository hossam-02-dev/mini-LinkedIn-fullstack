package com.example.miniLinkedin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.NotificationEntity;

@Repository
public interface NotificationRepository  extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByDestinataireIdOrderByDateDesc(Long destinataireId);

    
    List<NotificationEntity> findByDestinataireIdAndLu(Long destinataireId, boolean lu);

   
    long countByDestinataireIdAndLu(Long destinataireId, boolean lu);


    void deleteByDestinataireId(Long destinataireId);
	
	
}
