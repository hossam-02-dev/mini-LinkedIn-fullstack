package com.example.miniLinkedin.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniLinkedin.dtos.ProfilRequestDto;
import com.example.miniLinkedin.dtos.ProfilResponseDto;
import com.example.miniLinkedin.dtos.ProfileStatsDto;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.ProfileViewEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.exceptions.ResourceNotFoundException;
import com.example.miniLinkedin.mapping.ProfilMapper;
import com.example.miniLinkedin.repositories.ProfilRepository;
import com.example.miniLinkedin.repositories.ProfileViewRepository;
import com.example.miniLinkedin.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfilService {

    private final ProfilRepository profilRepository;
    private final ProfilMapper profilMapper;
    private final UserRepository userRepository;
    private final ProfileViewRepository profileViewRepository;
 // À injecter via @RequiredArgsConstructor :
 // private final ProfileViewRepository profileViewRepository;

 @Transactional
 public void recordProfileView(Long viewedUserId, Long currentViewerId) {
     System.out.println("--- DEBUG recordProfileView ---");
     System.out.println("Viewer ID: " + currentViewerId);
     System.out.println("Viewed User ID: " + viewedUserId);

     if (currentViewerId == null) {
         System.out.println("Viewer ID is null. Aborting.");
         return;
     }
     
     ProfilEntity viewedProfile = profilRepository.findByUserId(viewedUserId).orElse(null);
     if (viewedProfile == null) {
         System.out.println("Viewed Profile not found for User ID: " + viewedUserId + ". Aborting.");
         return;
     }

     System.out.println("Viewed Profile ID: " + viewedProfile.getId());

     // Ne pas compter si l'utilisateur regarde son propre profil
     if (viewedProfile.getUser().getId().equals(currentViewerId)) {
         System.out.println("User is viewing their own profile. Aborting.");
         return;
     }

     // Ne pas compter si l'utilisateur a déjà vu ce profil dans les 12 dernières heures (anti-spam)
     // LocalDateTime twelveHoursAgo = LocalDateTime.now().minusHours(12);
     // if (profileViewRepository.existsByViewerIdAndViewedProfileIdAndViewedAtAfter(currentViewerId, viewedProfile.getId(), twelveHoursAgo)) {
     //    System.out.println("Anti-spam triggered. Aborting.");
     //    return;
     // }

     UserEntity viewer = userRepository.findById(currentViewerId).orElse(null);
     if (viewer == null) {
         System.out.println("Viewer entity not found. Aborting.");
         return;
     }

     // Sauvegarder la vue
     ProfileViewEntity view = ProfileViewEntity.builder()
             .viewer(viewer)
             .viewedProfile(viewedProfile)
             .viewedAt(LocalDateTime.now())
             .build();
     
     profileViewRepository.save(view);
     System.out.println("SUCCESS: Profile view saved in DB!");
 }

 @Transactional(readOnly = true)
 public ProfileStatsDto getMyProfileStats(Long currentUserId) {
     System.out.println("--- DEBUG getMyProfileStats ---");
     System.out.println("Fetching stats for User ID: " + currentUserId);

     ProfilEntity myProfile = userRepository.findById(currentUserId)
             .orElseThrow(() -> new ResourceNotFoundException("User not found"))
             .getProfile();
             
     if (myProfile == null) {
         System.out.println("myProfile is null, returning empty stats.");
         return ProfileStatsDto.builder().build();
     }

     System.out.println("My Profile ID: " + myProfile.getId());

     LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
     long totalViews = profileViewRepository.countViewsByProfilIdSince(myProfile.getId(), thirtyDaysAgo);
     
     System.out.println("Total views found in DB: " + totalViews);

     List<ProfileViewEntity> recentViews = profileViewRepository.findTop5RecentViewers(myProfile.getId());
     System.out.println("Recent viewers count: " + recentViews.size());
     
     List<ProfileStatsDto.ViewerDto> recentViewersDto = recentViews.stream().map(view -> {
         UserEntity viewer = view.getViewer();
         ProfilEntity viewerProfile = viewer.getProfile();
         return ProfileStatsDto.ViewerDto.builder()
                 .firstName(viewer.getFirstName())
                 .lastName(viewer.getLastName())
                 .title(viewerProfile != null ? viewerProfile.getName() : "")
                 .timeAgo("Récemment") // On pourrait formater view.getViewedAt() ici
                 .build();
     }).toList();

     return ProfileStatsDto.builder()
             .totalViewsLast30Days(totalViews)
             .recentViewers(recentViewersDto)
             .build();
 }
 
 
 
 
 
 

    @Transactional
    public ProfilResponseDto getProfilByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        ProfilEntity profil = user.getProfile();
        if (profil == null) {
            // Créer un profil par défaut et l'associer
            profil = new ProfilEntity();
            profil.setUser(user);
            profil.setName(user.getFirstName() + " " + user.getLastName());
            profil.setBio("");
            profil.setVille("");
            profil.setEtablissement("");
            profil.setSiteWeb("");
            profil.setPhotoUrl("");
           // profil.setDateNaissance(null);
            profil.setDateNaissance(LocalDate.now()); // ou LocalDate.of(1900,1,1)           
            profil = profilRepository.save(profil);
            
            // Lier le profil à l'utilisateur
            user.setProfile(profil);
            userRepository.save(user);
        } else {
            boolean updated = false;
            if (profil.getDateNaissance() == null) {
                profil.setDateNaissance(LocalDate.now());
                updated = true;
            }
            if (profil.getSiteWeb() == null) {
                profil.setSiteWeb("");
                updated = true;
            }
            if (profil.getName() == null) {
                profil.setName(user.getFirstName() + " " + user.getLastName());
                updated = true;
            }
            if (profil.getVille() == null) {
                profil.setVille("");
                updated = true;
            }
            if (profil.getEtablissement() == null) {
                profil.setEtablissement("");
                updated = true;
            }
            if (profil.getPhotoUrl() == null) {
                profil.setPhotoUrl("");
                updated = true;
            }
            if (profil.getBio() == null) {
                profil.setBio("");
                updated = true;
            }
            if (updated) {
                profilRepository.save(profil);
            }
        }
        return profilMapper.toDto(profil);
    }
    @Transactional
    public ProfilResponseDto createProfil(Long userId, ProfilRequestDto dto) {
        // Vérifier si l'utilisateur a déjà un profil (optionnel)
        if (profilRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Cet utilisateur a déjà un profil");
        }
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ProfilEntity profil = new ProfilEntity();
        profil.setUser(user);
        profil.setName(dto.getName());
        profil.setVille(dto.getVille());
        profil.setEtablissement(dto.getEtablissement());
        profil.setBio(dto.getBio());
        profil.setSiteWeb(dto.getSiteWeb());
        profil.setPhotoUrl(dto.getPhotoUrl());
        profil.setDateNaissance(dto.getDateNaissance());
        profilRepository.save(profil);
        return profilMapper.toDto(profil);
    }

    @Transactional
    public ProfilResponseDto updateProfil(Long profilId, ProfilRequestDto dto) {
        // Ici profilId est l'ID du profil (pas celui de l'utilisateur)
        ProfilEntity profil = profilRepository.findById(profilId)
            .orElseThrow(() -> new ResourceNotFoundException("Profil not found with id: " + profilId));
        profil.setName(dto.getName());
        profil.setVille(dto.getVille());
        profil.setEtablissement(dto.getEtablissement());
        profil.setBio(dto.getBio());
        profil.setSiteWeb(dto.getSiteWeb());
        profil.setPhotoUrl(dto.getPhotoUrl());
        profil.setDateNaissance(dto.getDateNaissance());
        profilRepository.save(profil);
        return profilMapper.toDto(profil);
    }

    @Transactional
    public ProfilResponseDto uploadPhoto(Long profilId, String photoUrl) {
        // Ici aussi, profilId est l'ID du profil
        ProfilEntity profil = profilRepository.findById(profilId)
            .orElseThrow(() -> new ResourceNotFoundException("Profil not found with id: " + profilId));
        profil.setPhotoUrl(photoUrl);
        profilRepository.save(profil);
        return profilMapper.toDto(profil);
    }
}