package com.example.miniLinkedin.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.ProjetRequestDto;
import com.example.miniLinkedin.dtos.ProjetResponseDto;
import com.example.miniLinkedin.entities.ProjetEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.repositories.ProjetRepository;
import com.example.miniLinkedin.repositories.UserRepository;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.ProjetMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetService {
	
private final ProjetRepository projetRepository;
private  final UserRepository userRepository;
private final ProjetMapper projetMapper;

@Transactional
public ProjetResponseDto publierProjet(Long auteurId , ProjetRequestDto projetRequestDto) {
	UserEntity user = userRepository.findById(auteurId)
			
.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + auteurId));
	ProjetEntity projet = new ProjetEntity();
	projet.setTitre(projetRequestDto.getTitre());
	projet.setDescription(projetRequestDto.getDescription());
	projet.setTechnologies(projetRequestDto.getTechnologies());
	projet.setImageUrl(projetRequestDto.getImageUrl());
	projet.setLienGithub(projetRequestDto.getLienGithub());
	projet.setUser(user);
	projet.setLienDemo(projetRequestDto.getLienDemo());
	projet.setDateCreation(LocalDateTime.now());
	projet.setDateMaj(LocalDateTime.now());
	
	ProjetEntity publishedProject = projetRepository.save(projet);
	return projetMapper.toDto(publishedProject);
}

@Transactional
public ProjetResponseDto updateProjet(Long projetId , Long userId, ProjetRequestDto projetRequestDto) {
	
ProjetEntity projet = projetRepository.findById(projetId)
.orElseThrow(() -> new ResourceNotFoundException("Projet not found with id: " + projetId));

if (!projet.getUser().getId().equals(userId)) {
    throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce projet");
}
projet.setTitre(projetRequestDto.getTitre());
projet.setDescription(projetRequestDto.getDescription());
projet.setTechnologies(projetRequestDto.getTechnologies());
projet.setLienGithub(projetRequestDto.getLienGithub());
projet.setLienDemo(projetRequestDto.getLienDemo());
projet.setImageUrl(projetRequestDto.getImageUrl());
projet.setDateMaj(LocalDateTime.now());
projet.setUser(projet.getUser());

	ProjetEntity updatedProject = projetRepository.save(projet);
	return projetMapper.toDto(updatedProject);
}


@Transactional(readOnly = true)
public ProjetResponseDto getProjetById(Long projetId) {
	ProjetEntity projet = projetRepository.findById(projetId)
			.orElseThrow(() -> new ResourceNotFoundException("Projet not found with id: " + projetId));
	return projetMapper.toDto(projet);
}

@Transactional(readOnly = true)
public List<ProjetResponseDto> getProjetsByUserId(Long userId) {
	
	userRepository.findById(userId)
    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
	
	List<ProjetEntity> projets = projetRepository.findByUserId(userId);
	
	return projetMapper.toDtoList(projets);
}

@Transactional(readOnly = true)
public List<ProjetResponseDto> getAllProjets() {
	List<ProjetEntity> projets = projetRepository.findAll();
	return projetMapper.toDtoList(projets);
}

@Transactional(readOnly = true)
public List<ProjetResponseDto> getProjectsByTitle(String titre) {
    List<ProjetEntity> projets = projetRepository.findByTitreContainingIgnoreCase(titre);
    		
    return projetMapper.toDtoList(projets);
}



@Transactional
public void deleteProjet(Long projetId , Long userId) {
	ProjetEntity projet = projetRepository.findById(projetId)
			.orElseThrow(() -> new ResourceNotFoundException("Projet not found with id: " + projetId));
	
	if (!projet.getUser().getId().equals(userId)) {
	    throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce projet");
	}
	projetRepository.delete(projet);
}

}
