package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.PublicationRequestDto;
import com.example.miniLinkedin.dtos.PublicationResponseDto;
import com.example.miniLinkedin.entities.PublicationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.PublicationMapper;
import com.example.miniLinkedin.repositories.CommentaireRepository;
import com.example.miniLinkedin.repositories.LikeRepository;
import com.example.miniLinkedin.repositories.PublicationRepository;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentaireRepository commentaireRepository;

    // Méthode utilitaire pour enrichir un DTO avec nom et photo de l'auteur
    private PublicationResponseDto enrichirAuteur(PublicationEntity publication, PublicationResponseDto dto) {
        UserEntity auteur = publication.getAuteur();
        dto.setNomAuteur(auteur.getFirstName() + " " + auteur.getLastName());
        if (auteur.getProfile() != null) {
            dto.setPhotoProfil(auteur.getProfile().getPhotoUrl());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PublicationResponseDto getPublicationById(Long publicationId) {
        PublicationEntity publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + publicationId));
        PublicationResponseDto dto = publicationMapper.toDto(publication);
        return enrichirAuteur(publication, dto);
    }

    @Transactional(readOnly = true)
    public List<PublicationResponseDto> getPublicationsByAuteurId(Long auteurId) {
        List<PublicationEntity> publications = publicationRepository.findByAuteurIdOrderByDatePublicationDesc(auteurId);
        return publications.stream()
                .map(p -> {
                    PublicationResponseDto dto = publicationMapper.toDto(p);
                    return enrichirAuteur(p, dto);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PublicationResponseDto createPublication(Long auteurId, PublicationRequestDto publicationRequestDto) {
        UserEntity auteur = userRepository.findById(auteurId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + auteurId));
        PublicationEntity publication = new PublicationEntity();
        publication.setAuteur(auteur);
        publication.setContenu(publicationRequestDto.getContenu());
        publication.setDatePublication(LocalDateTime.now());
        publication.setDateMaj(LocalDateTime.now());
        publication.setImageUrl(publicationRequestDto.getImageUrl());
        PublicationEntity savedPublication = publicationRepository.save(publication);
        PublicationResponseDto dto = publicationMapper.toDto(savedPublication);
        return enrichirAuteur(savedPublication, dto);
    }

    @Transactional
    public PublicationResponseDto updatePublication(Long publicationId, Long userId, PublicationRequestDto publicationRequestDto) {
        PublicationEntity publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + publicationId));
        if (!publication.getAuteur().getId().equals(userId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier/supprimer cette publication");
        }
        publication.setContenu(publicationRequestDto.getContenu());
        publication.setDateMaj(LocalDateTime.now());
        publication.setImageUrl(publicationRequestDto.getImageUrl());
        PublicationEntity updatedPublication = publicationRepository.save(publication);
        PublicationResponseDto dto = publicationMapper.toDto(updatedPublication);
        return enrichirAuteur(updatedPublication, dto);
    }

    @Transactional(readOnly = true)
    public List<PublicationResponseDto> getFeed() {
        List<PublicationEntity> publications = publicationRepository.findAllByOrderByDatePublicationDesc();
        return publications.stream()
                .map(p -> {
                    PublicationResponseDto dto = publicationMapper.toDto(p);
                    return enrichirAuteur(p, dto);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePublication(Long publicationId, Long userId) {
        PublicationEntity publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + publicationId));
        if (!publication.getAuteur().getId().equals(userId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier/supprimer cette publication");
        }
        likeRepository.deleteByPublicationId(publicationId);
        commentaireRepository.deleteByPublicationId(publicationId);
        publicationRepository.delete(publication);
    }
}