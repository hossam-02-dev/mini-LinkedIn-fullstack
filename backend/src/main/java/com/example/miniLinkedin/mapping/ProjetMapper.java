package com.example.miniLinkedin.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.miniLinkedin.dtos.ProjetRequestDto;
import com.example.miniLinkedin.dtos.ProjetResponseDto;
import com.example.miniLinkedin.entities.ProjetEntity;
import com.example.miniLinkedin.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface ProjetMapper {
	
	@Mapping(source = "user.id",    target = "auteurId")
	@Mapping(target = "nomAuteur",  source = "user", qualifiedByName = "mapNomAuteur")
	
	ProjetResponseDto toDto(ProjetEntity projet);
	
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)          
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateMaj", ignore = true)
	ProjetEntity toEntity(ProjetRequestDto requestDto);
	
	List<ProjetResponseDto> toDtoList(List<ProjetEntity> entities);
	
	
	
	@Named("mapNomAuteur")
	default String mapNomAuteur(UserEntity user) {
	    if (user == null) return null;
	    String prenom = user.getFirstName() != null ? user.getFirstName() : "";
	    String nom    = user.getLastName()  != null ? user.getLastName()  : "";
	    return (prenom + " " + nom).trim();
	}

}
