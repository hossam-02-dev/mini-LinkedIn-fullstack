package com.example.miniLinkedin.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.miniLinkedin.dtos.ProfilRequestDto;
import com.example.miniLinkedin.dtos.ProfilResponseDto;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface ProfilMapper {
	
@Mapping(source = "user.id" , target = "userId")
@Mapping(target = "nomComplet", source = "user", qualifiedByName = "mapNomComplet")
ProfilResponseDto toDto (ProfilEntity profil);

@Mapping(target = "id" , ignore = true)
@Mapping(target = "user",  ignore = true)
@Mapping(target = "formations", ignore = true)
ProfilEntity toEntity (ProfilRequestDto dto);

@Named("mapNomComplet")

default String mapNomComplet(UserEntity user) {
	
    if (user == null) return null;
    String prenom = (user.getFirstName() != null) ? user.getFirstName() : "";
    String nom = (user.getLastName() != null) ? user.getLastName() : "";
    return (prenom + " " + nom).trim();
}

}
