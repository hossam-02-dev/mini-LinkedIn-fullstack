package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.miniLinkedin.dtos.LikeResponseDto;
import com.example.miniLinkedin.entities.LikeEntity;
import com.example.miniLinkedin.entities.PublicationEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.LikeMapper;
import com.example.miniLinkedin.repositories.LikeRepository;
import com.example.miniLinkedin.repositories.PublicationRepository;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
	
private final LikeRepository likeRepository;
private final PublicationRepository publicationRepository;
private final UserRepository userRepository;
private final LikeMapper likeMapper;

@Transactional
public LikeResponseDto addLike(Long publicationId, Long userId) {
    if (userId == null) {
        throw new IllegalArgumentException("User not authenticated");
    }
    UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    // Si le like existe déjà, on le retourne simplement (pas d’erreur)
    Optional<LikeEntity> existing = likeRepository.findByUserIdAndPublicationId(userId, publicationId);
    if (existing.isPresent()) {
        return likeMapper.toDto(existing.get());
    }

    PublicationEntity publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + publicationId));

    LikeEntity like = new LikeEntity();
    like.setUser(user);
    like.setPublication(publication);
    like.setCreatedAt(LocalDateTime.now());
    likeRepository.save(like);
    return likeMapper.toDto(like);
}

@Transactional(readOnly = true)

public long countLikesByPublication(Long publicationId) {
    publicationRepository.findById(publicationId)
        .orElseThrow(() -> new ResourceNotFoundException("Publication not found: " + publicationId));
    return likeRepository.countByPublicationId(publicationId);
}

@Transactional
public void removeLike (Long publicationId , Long userId) {
	
	LikeEntity like = likeRepository.findByUserIdAndPublicationId(userId, publicationId)
			.orElseThrow(() -> new ResourceNotFoundException("Like not found for user id: " + userId + " and publication id: " + publicationId));
	
	likeRepository.delete(like);

}
}
