package com.example.miniLinkedin.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.CompetenceRequestDto;
import com.example.miniLinkedin.dtos.CompetenceResponseDto;
import com.example.miniLinkedin.entities.CompetenceEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Niveau;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.CompetenceMapper;
import com.example.miniLinkedin.repositories.CompetenceRepository;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CompetenceService {
	
private final CompetenceRepository competenceRepository;
private final UserRepository userRepository;
private final CompetenceMapper competenceMapper;

@Transactional
public CompetenceResponseDto addCompetence( Long userId , CompetenceRequestDto competenceRequestDto) {
	UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
	
	if(competenceRepository.existsByNomAndUserId(competenceRequestDto.getNom(), userId)) {
	throw new IllegalStateException("Utilisateur a déjà cette compétence");	
		
	}
	
	CompetenceEntity competence = new CompetenceEntity();
	competence.setNom(competenceRequestDto.getNom());
	competence.setUser(user);
	competence.setNiveau(Niveau.valueOf(competenceRequestDto.getNiveau())); 
	competenceRepository.save(competence);
	return competenceMapper.toDto(competence);

}

@Transactional
public CompetenceResponseDto updateCompetence(Long competenceId , Long userId , CompetenceRequestDto competenceRequestDto) {
	
	CompetenceEntity competence = competenceRepository.findById(competenceId)
			.orElseThrow(() -> new ResourceNotFoundException("Compétence non trouvée avec l'ID: " + competenceId));
	if (!competence.getUser().getId().equals(userId)) {
	    throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette compétence");
	}
	competence.setNom(competenceRequestDto.getNom());
	competence.setNiveau(Niveau.valueOf(competenceRequestDto.getNiveau()));

CompetenceEntity updated = competenceRepository.save(competence);
return competenceMapper.toDto(updated);			
	
}

@Transactional(readOnly = true)
public List<CompetenceResponseDto> getCompetencesByUserId(Long userId) {
	
	 userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
	 
	List<CompetenceEntity> competences = competenceRepository.findByUserId(userId);
	return competenceMapper.toDtoList(competences);
}

@Transactional
public void deleteCompetence(Long competenceId , Long userId) {
CompetenceEntity competence = competenceRepository.findById(competenceId)
		.orElseThrow(() -> new ResourceNotFoundException("Compétence non trouvée avec l'ID: " + competenceId));

if (!competence.getUser().getId().equals(userId)) {
    throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette compétence");
}
	competenceRepository.delete(competence);
}

}
