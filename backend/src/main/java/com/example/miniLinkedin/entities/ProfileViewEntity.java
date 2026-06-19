package com.example.miniLinkedin.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'utilisateur qui visite le profil
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id", nullable = false)
    private UserEntity viewer;

    // Le profil qui est visité
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewed_profile_id", nullable = false)
    private ProfilEntity viewedProfile;

    @Column(nullable = false)
    private LocalDateTime viewedAt;
}