package com.example.miniLinkedin.entities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.miniLinkedin.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

@Table(name = "users")
public class UserEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String lastName;
	
	private String activationToken;
	
	private String refreshToken;
	
	private LocalDateTime refreshTokenExpiry;
	
	@Column(nullable = false)
	private String firstName;
	
		@Column(nullable = false, unique = true)
	private String email;
		
		@Column(nullable = false)
	private String password;
		
		@Column(nullable = false)
	private LocalDateTime createdAt;
		
		@Column(nullable = false)
		@Enumerated(EnumType.STRING)
		private Role role;
		
		
		private boolean isActive;
		
@OneToOne
@JoinColumn(name = "profile_id")
private ProfilEntity profile;

@OneToMany(mappedBy = "user")
private List<CompetenceEntity> competences;

@OneToMany(mappedBy = "user")
private List<ProjetEntity> projets;

@OneToMany(mappedBy = "auteur")
private  List<CommentaireEntity> commentaires;

@OneToMany(mappedBy = "user")
private List<LikeEntity> likes;

@OneToMany(mappedBy = "demandeur")
private List<ConnexionEntity> connexionsEnvoyees;

@OneToMany(mappedBy = "destinataire")
private List<ConnexionEntity> connexionsRecues;

@OneToMany(mappedBy = "expediteur")
private List<MessageEntity> messagesEnvoyes;

@OneToMany(mappedBy = "destinataire")
private List<MessageEntity> messagesRecus;

@OneToMany(mappedBy = "destinataire")
private List<NotificationEntity> notificationsRecues;

@OneToMany(mappedBy = "declencheur")
private List<NotificationEntity> notificationsDeclenchees;

@OneToMany(mappedBy = "auteur")
private List<PublicationEntity> publications;

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
	
	return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}

@Override
public String getUsername() {
	
	return email;
}

@Override
public String getPassword() {
	return  password;
}

@Override
public boolean isAccountNonExpired() {
	return true;

}

@Override
public boolean isAccountNonLocked() {
	return true;
	
}

@Override

public boolean isCredentialsNonExpired() {
	return true;
	
}

@Override

public boolean isEnabled() {
	return isActive;
	
}





	

}
