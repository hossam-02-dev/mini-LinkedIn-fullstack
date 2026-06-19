package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.ConnexionResponseDto;
import com.example.miniLinkedin.entities.ConnexionEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.StatutConnexion;
import com.example.miniLinkedin.mapping.ConnexionMapper;
import com.example.miniLinkedin.repositories.ConnexionRepository;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnexionService {
	
private final ConnexionRepository connexionRepository;
private  final NotificationService notificationService;
private final ConnexionMapper connexionMapper;
private final  UserRepository userRepository;

@Transactional
public ConnexionResponseDto envoyerDemande (Long demandeurId , Long destinataireId) {
	
if(demandeurId.equals(destinataireId)) {
	throw new IllegalStateException("Impossible d'envoyer une demande de connexion le demandeur est le destinataire ");
}	

if(connexionRepository.existsByDemandeurIdAndDestinataireId(demandeurId, destinataireId)) {
	throw new IllegalStateException("Une demande de connexion a déjà été envoyée à cet utilisateur.");
}

UserEntity demandeur = userRepository.findById(demandeurId)
.orElseThrow(() -> new  ResourceNotFoundException("le demandeur ayant l'id" + demandeurId + " est introuvable"));


UserEntity destinataire = userRepository.findById(destinataireId)
.orElseThrow(() -> new  ResourceNotFoundException("le destinataire ayant l'id " + destinataireId + " est introuvable"));

ConnexionEntity connexion = new ConnexionEntity () ;

connexion.setDemandeur(demandeur);
connexion.setDestinataire(destinataire);
connexion.setStatut(StatutConnexion.EN_ATTENTE);
connexion.setDateEnvoi(LocalDateTime.now());

ConnexionEntity savedConnexion =  connexionRepository.save(connexion) ;

notificationService.createNotification(
        destinataireId, 
        demandeurId, 
        "Nouvelle demande de connexion", 
        "CONNEXION"
    );
return connexionMapper.toDto(savedConnexion);

}


public String getConnectionStatus(Long currentUserId, Long targetUserId) {
    Optional<ConnexionEntity> connexion = connexionRepository
        .findByDemandeurIdAndDestinataireId(currentUserId, targetUserId)
        .or(() -> connexionRepository.findByDemandeurIdAndDestinataireId(targetUserId, currentUserId));
    if (connexion.isEmpty()) return "default";
    ConnexionEntity conn = connexion.get();
    switch (conn.getStatut()) {
        case EN_ATTENTE:
            return conn.getDemandeur().getId().equals(currentUserId) ? "pending" : "received";
        case ACCEPTEE:
            return "connected";
        default:
            return "default";
    }
}

public Long getConnexionId(Long currentUserId, Long targetUserId) {
    return connexionRepository
        .findByDemandeurIdAndDestinataireId(currentUserId, targetUserId)
        .or(() -> connexionRepository.findByDemandeurIdAndDestinataireId(targetUserId, currentUserId))
        .map(ConnexionEntity::getId)
        .orElse(null);
}

@Transactional
public ConnexionResponseDto accepterConnexion(Long connexionId, Long userId) {
    
    ConnexionEntity connexion = connexionRepository.findById(connexionId)
            .orElseThrow(() -> new ResourceNotFoundException("La connexion ayant l'ID " + connexionId + " est introuvable"));

    if (!connexion.getDestinataire().getId().equals(userId)) {
        throw new UnauthorizedException("Vous n'êtes pas autorisé à accepter cette demande car vous n'en êtes pas le destinataire.");
    }

   
    if (connexion.getStatut() != StatutConnexion.EN_ATTENTE) {
        throw new IllegalStateException("Cette demande de connexion ne peut plus être acceptée (Statut actuel : " + connexion.getStatut() + ")");
    }

    connexion.setStatut(StatutConnexion.ACCEPTEE);
    connexion.setDateReponse(LocalDateTime.now()); // Assurez-vous d'avoir ce champ dans votre Entité

    
    ConnexionEntity savedConnexion = connexionRepository.save(connexion);

    notificationService.createNotification(
        connexion.getDemandeur().getId(), 
        userId, 
        "a accepté votre demande de connexion", 
        "CONNEXION"
    );

   
    return connexionMapper.toDto(savedConnexion);
}
@Transactional
public ConnexionResponseDto refuserDemande(Long connexionId, Long userId) {
    
    
    ConnexionEntity connexion = connexionRepository.findById(connexionId)
            .orElseThrow(() -> new ResourceNotFoundException("La demande de connexion avec l'ID " + connexionId + " est introuvable"));

   
    if (!connexion.getDestinataire().getId().equals(userId)) {
        throw new UnauthorizedException("Vous n'êtes pas autorisé à refuser cette demande.");
    }

    
    if (connexion.getStatut() != StatutConnexion.EN_ATTENTE) {
        throw new IllegalStateException("Cette demande ne peut pas être refusée car son statut actuel est : " + connexion.getStatut());
    }

   
    connexion.setStatut(StatutConnexion.REFUSEE);
    connexion.setDateReponse(LocalDateTime.now());

    
    ConnexionEntity savedConnexion = connexionRepository.save(connexion);

    return connexionMapper.toDto(savedConnexion);
}

@Transactional
public List<ConnexionResponseDto> getConnexionsAcceptees(Long userId) {
    
   
    if (!userRepository.existsById(userId)) {
        throw new ResourceNotFoundException("L'utilisateur avec l'ID " + userId + " est introuvable");
    }


    List<ConnexionEntity> connexions = connexionRepository.findConnexionsAccepteesParUserId(userId);

    return connexionMapper.toDtoList(connexions);
}
@Transactional
public void annulerDemande(Long connexionId, Long userId) {
    
    ConnexionEntity connexion = connexionRepository.findById(connexionId)
            .orElseThrow(() -> new ResourceNotFoundException("La demande de connexion avec l'ID " + connexionId + " est introuvable"));


    if (!connexion.getDemandeur().getId().equals(userId)) {
        throw new UnauthorizedException("Vous n'êtes pas autorisé à annuler cette demande car vous n'en êtes pas l'auteur.");
    }

    
    if (connexion.getStatut() != StatutConnexion.EN_ATTENTE) {
        throw new IllegalStateException("Impossible d'annuler cette demande car elle a déjà été traitée (Statut : " + connexion.getStatut() + ")");
    }

  
    connexionRepository.delete(connexion);
    

}
@Transactional
public List<ConnexionResponseDto> getDemandesRecues(Long userId) {
    
   
    if (!userRepository.existsById(userId)) {
        throw new ResourceNotFoundException("L'utilisateur avec l'ID " + userId + " est introuvable");
    }

    List<ConnexionEntity> demandes = connexionRepository.findByDestinataireIdAndStatut(userId, StatutConnexion.EN_ATTENTE);

   
    return connexionMapper.toDtoList(demandes);
}
@Transactional
public void supprimerConnexion(Long connexionId, Long userId) {
    ConnexionEntity connexion = connexionRepository.findById(connexionId)
            .orElseThrow(() -> new ResourceNotFoundException("La connexion avec l'ID " + connexionId + " est introuvable"));

    // On vérifie que l'utilisateur est bien l'un des deux amis concernés
    if (!connexion.getDemandeur().getId().equals(userId) && !connexion.getDestinataire().getId().equals(userId)) {
        throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette connexion.");
    }

    // On supprime la relation
    connexionRepository.delete(connexion);
}

}
