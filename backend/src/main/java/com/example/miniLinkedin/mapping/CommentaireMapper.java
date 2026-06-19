package com.example.miniLinkedin.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.miniLinkedin.dtos.CommentaireRequestDto;
import com.example.miniLinkedin.dtos.CommentaireResponseDto;
import com.example.miniLinkedin.entities.CommentaireEntity;

@Mapper(componentModel = "spring") 

public interface CommentaireMapper {
	
	@Mapping(target = "auteur",    ignore = true)
	@Mapping(target = "publication", ignore = true)
	@Mapping(target = "date",     ignore = true)
	@Mapping(target = "id",    ignore = true)
	CommentaireEntity toEntity(CommentaireRequestDto dto);
	
	@Mapping(source = "auteur.lastName", target = "nomAuteur")
	@Mapping(source = "auteur.id", target = "auteurId")
	CommentaireResponseDto toDto(CommentaireEntity entity);
	
	
	List<CommentaireResponseDto> toDtoList(List<CommentaireEntity> entities);
}
