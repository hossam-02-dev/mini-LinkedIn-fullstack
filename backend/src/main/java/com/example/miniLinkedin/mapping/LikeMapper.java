package com.example.miniLinkedin.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.miniLinkedin.dtos.LikeRequestDto;
import com.example.miniLinkedin.dtos.LikeResponseDto;
import com.example.miniLinkedin.entities.LikeEntity;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "user.id",        target = "userId")
    @Mapping(source = "publication.id", target = "publicationId")
    LikeResponseDto toDto(LikeEntity entity);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "user",        ignore = true)
    @Mapping(target = "publication", ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    LikeEntity toEntity(LikeRequestDto dto);
}