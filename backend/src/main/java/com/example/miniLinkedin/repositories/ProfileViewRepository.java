package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.ProfileViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfileViewRepository extends JpaRepository<ProfileViewEntity, Long> {

    // Compter les vues d'un profil sur les X derniers jours
    @Query("SELECT COUNT(v) FROM ProfileViewEntity v WHERE v.viewedProfile.id = :profilId AND v.viewedAt >= :since")
    long countViewsByProfilIdSince(@Param("profilId") Long profilId, @Param("since") LocalDateTime since);

    // Vérifier si un utilisateur a déjà vu ce profil récemment (pour éviter les doublons de clics)
    boolean existsByViewerIdAndViewedProfileIdAndViewedAtAfter(Long viewerId, Long profilId, LocalDateTime since);

    // Récupérer les 5 dernières personnes ayant visité le profil
    @Query("SELECT v FROM ProfileViewEntity v WHERE v.viewedProfile.id = :profilId ORDER BY v.viewedAt DESC LIMIT 5")
    List<ProfileViewEntity> findTop5RecentViewers(@Param("profilId") Long profilId);
}