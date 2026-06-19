package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.miniLinkedin.dtos.NotificationResponseDto;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.NotificationMapper;
import com.example.miniLinkedin.repositories.NotificationRepository;

import com.example.miniLinkedin.repositories.UserRepository;
import com.example.miniLinkedin.entities.NotificationEntity;
import com.example.miniLinkedin.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
 
public class NotificationService {
	
private final NotificationRepository notificationRepository;
private final UserRepository userRepository;
private final NotificationMapper notificationMapper;
private final WebSocketNotificationService webSocketNotificationService;

@Transactional(readOnly = true)
public NotificationResponseDto getNotificationById(Long id) {
	
	NotificationEntity notification = notificationRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("La notification ayant l'id :  " + id + " est introuvable"));
	return notificationMapper.toDto(notification);
}

 @Transactional(readOnly = true)
 public List<NotificationResponseDto> getNotificationsByDestinataireId(Long destinataireId) {
	 
	 UserEntity  destinataire  = userRepository.findById(destinataireId)
		.orElseThrow(() -> new ResourceNotFoundException("Le destinataire ayant l'id :  " + destinataireId + " est introuvable"));
	 
	 List<NotificationEntity> notifications = notificationRepository.findByDestinataireIdOrderByDateDesc(destinataireId);
	 return notificationMapper.toDtoList(notifications);
 }

@Transactional
public NotificationResponseDto createNotification(Long destinataireId , Long declencheurId
, String contenu , String type	) {
	
	if(destinataireId.equals(declencheurId)) {
		
		System.out.println("Notification ignorée... ");	
			return null;
		}
	
UserEntity destinataire  =  userRepository.findById(destinataireId)

.orElseThrow(() -> new ResourceNotFoundException("Le destinataire ayant l'id :  " + destinataireId+ " est introuvable"));

  
  
UserEntity declencheur  =  userRepository.findById(declencheurId)


.orElseThrow(() -> new ResourceNotFoundException("Le déclencheur ayant l'id :  " + declencheurId+ " est introuvable"));	


NotificationEntity notification = new  NotificationEntity ();
notification.setDestinataire(destinataire);
notification.setDeclencheur(declencheur);
notification.setContenu(contenu);
notification.setType(type);
notification.setLu(false);
notification.setDate(LocalDateTime.now());

NotificationEntity savedNotification = notificationRepository.save(notification);

NotificationResponseDto dto = notificationMapper.toDto(savedNotification);
webSocketNotificationService.envoyerNotification(destinataireId, dto);

return dto;

}
@Transactional
public NotificationResponseDto markNotificationAsRead(Long notificationId, Long userId) {

    NotificationEntity notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "La notification ayant l'id : " + notificationId + " est introuvable"));

    if (!notification.getDestinataire().getId().equals(userId)) {
        throw new UnauthorizedException(
            "Vous n'êtes pas autorisé à modifier cette notification");
    }

    notification.setLu(true);
    notificationRepository.save(notification);

    return notificationMapper.toDto(notification);
}


@Transactional
public void deleteNotification (Long notificationId , Long userId) {
	
NotificationEntity notification = notificationRepository.findById(notificationId)
.orElseThrow(() -> new ResourceNotFoundException("La notification ayant l'id :  " + notificationId+ " est introuvable"));

if (!notification.getDestinataire().getId().equals(userId)) {
	
    throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette notification");
}
notificationRepository.delete(notification);

}

@Transactional(readOnly = true)
public Long countUnreadNotifications(Long userId) {
	 userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("L'utilisateur ayant l'id :  " + userId + " est introuvable"));
	return notificationRepository.countByDestinataireIdAndLu(userId, false);
}
	
}


