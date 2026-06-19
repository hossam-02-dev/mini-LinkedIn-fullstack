package com.example.miniLinkedin.services;


import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.miniLinkedin.dtos.FormationRequestDto;
import com.example.miniLinkedin.dtos.FormationResponseDto;
import com.example.miniLinkedin.entities.FormationEntity;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.exceptions.UnauthorizedException;
import com.example.miniLinkedin.mapping.FormationMapper;
import com.example.miniLinkedin.repositories.FormationRepository;
import com.example.miniLinkedin.repositories.ProfilRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class FormationService {
	
private final FormationRepository formationRepository;
private final ProfilRepository profilRepository;
private final FormationMapper formationMapper;

@Transactional
public FormationResponseDto  addFormation (Long profilId , FormationRequestDto formationRequestDto) {
	
ProfilEntity profil = profilRepository.findById(profilId)
.orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé avec l'ID: " + profilId));

if (formationRequestDto.getDateFin() != null && formationRequestDto.getDateFin().isBefore(formationRequestDto.getDateDebut())) {
    throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début.");
}

 FormationEntity formation = new FormationEntity();
 formation.setDomaine(formationRequestDto.getDomaine());
 formation.setEtablissement(formationRequestDto.getEtablissement());
 formation.setDateDebut(formationRequestDto.getDateDebut());
 formation.setDateFin(formationRequestDto.getDateFin());
 formation.setProfil(profil);
 formation.setDiplome(formationRequestDto.getDiplome());
 formation.setEnCours(formationRequestDto.getEnCours());
 
 FormationEntity savedFormation = formationRepository.save(formation);
 return formationMapper.toDto(savedFormation);
}
 @Transactional
 public FormationResponseDto updateFormation(Long formationId , Long userID , FormationRequestDto formationRequestDto) {
	FormationEntity formation = formationRepository.findById(formationId)
			.orElseThrow(() -> new ResourceNotFoundException("Formation non trouvée avec l'ID: " + formationId));
	
	if (!formation.getProfil().getUser().getId().equals(userID)) {
	    throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette formation");
	}
	
	if (formationRequestDto.getDateFin() != null && formationRequestDto.getDateFin().isBefore(formationRequestDto.getDateDebut())) {
	    throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début.");
	}
	
	
	 formation.setDomaine(formationRequestDto.getDomaine());
	 formation.setEtablissement(formationRequestDto.getEtablissement());
	 formation.setDiplome(formationRequestDto.getDiplome());
	 formation.setEnCours(formationRequestDto.getEnCours());
	 formation.setDateDebut(formationRequestDto.getDateDebut());
	 formation.setDateFin(formationRequestDto.getDateFin());
	 
	 FormationEntity savedFormation = formationRepository.save(formation);
	 return formationMapper.toDto(savedFormation);
	 
}
 @Transactional(readOnly = true)
 public List<FormationResponseDto>  getFormationsByProfilId(Long profilId) {
	 
	  profilRepository.findById(profilId)
			 .orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé avec l'ID: " + profilId));
	 
	  List<FormationEntity> formations = formationRepository.findByProfilId(profilId);
	 
	 return formationMapper.toDtoList(formations);
 }
 
 
 
 @Transactional
 public void deleteFormation(Long formationId , Long userID) {
	 
	FormationEntity formation = formationRepository.findById(formationId)
			.orElseThrow(() -> new ResourceNotFoundException("Formation non trouvée avec l'ID: " + formationId)); 
	
	if (!formation.getProfil().getUser().getId().equals(userID)) {
	    throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette formation");
	}
	
	
	formationRepository.delete(formation);
	 
 }

}
