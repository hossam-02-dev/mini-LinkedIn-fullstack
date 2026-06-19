package com.example.miniLinkedin.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.miniLinkedin.dtos.ConnexionRequestDto;
import com.example.miniLinkedin.dtos.ConnexionResponseDto;
import com.example.miniLinkedin.entities.ConnexionEntity;

@Mapper(componentModel = "spring")
public interface ConnexionMapper {
	
	@Mapping(source = "demandeur.id" , target = "demandeurId")
	@Mapping(source = "destinataire.id" , target = "destinataireId")
	@Mapping(target = "statutConnexion", expression = "java(entity.getStatut().name())")
	ConnexionResponseDto toDto(ConnexionEntity entity);
	
	List<ConnexionResponseDto> toDtoList(List<ConnexionEntity> entities);
	
	@Mapping(target = "id" , ignore = true)
	@Mapping(target = "demandeur" , ignore = true)
	@Mapping(target = "destinataire" , ignore = true)
	@Mapping(target = "statut" , ignore = true)
	@Mapping(target = "dateEnvoi" , ignore = true)
	@Mapping(target = "dateReponse" , ignore = true)
	ConnexionEntity toEntity(ConnexionRequestDto dto);

}
