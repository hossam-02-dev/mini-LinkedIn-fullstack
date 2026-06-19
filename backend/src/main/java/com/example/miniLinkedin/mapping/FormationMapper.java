package com.example.miniLinkedin.mapping;



import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.miniLinkedin.dtos.FormationRequestDto;
import com.example.miniLinkedin.dtos.FormationResponseDto;
import com.example.miniLinkedin.entities.FormationEntity;

@Mapper(componentModel = "spring")
public interface FormationMapper {
	
	@Mapping(source = "id", target = "id")
	
	@Mapping(source ="profil.id" , target = "profilId")
	FormationResponseDto toDto (FormationEntity formations);
	
	
	List<FormationResponseDto> toDtoList(List<FormationEntity> entities);
	
	@Mapping(target = "profil" , ignore = true)
	FormationEntity toEntity (FormationRequestDto dto);

}
