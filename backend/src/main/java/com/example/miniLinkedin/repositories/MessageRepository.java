package com.example.miniLinkedin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.MessageEntity;

@Repository

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

	
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.expediteur.id = :userId1 AND m.destinataire.id = :userId2) OR " +
           "(m.expediteur.id = :userId2 AND m.destinataire.id = :userId1) " +
           "ORDER BY m.dateEnvoi ASC")
    List<MessageEntity> findConversation(
        @Param("userId1") Long userId1,
        @Param("userId2") Long userId2);

    
    List<MessageEntity> findByDestinataireIdAndLu(Long destinataireId, boolean lu);
    
    List<MessageEntity> findByDestinataireIdAndLuFalse(Long userId);

    
    long countByDestinataireIdAndLu(Long destinataireId, boolean lu);

    
    List<MessageEntity> findByDestinataireIdOrderByDateEnvoiDesc(Long destinataireId);

   
    List<MessageEntity> findByExpediteurIdOrderByDateEnvoiDesc(Long expediteurId);
}

