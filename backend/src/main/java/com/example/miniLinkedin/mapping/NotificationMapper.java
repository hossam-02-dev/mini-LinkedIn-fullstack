package com.example.miniLinkedin.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.miniLinkedin.dtos.NotificationResponseDto;
import com.example.miniLinkedin.entities.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
	
List<NotificationResponseDto> toDtoList(List<NotificationEntity> entities);
	
@Mapping(source = "destinataire.id", target = "destinataireId")
@Mapping(source = "declencheur.id" , target = "declencheurId")

NotificationResponseDto toDto (NotificationEntity notification);


}
