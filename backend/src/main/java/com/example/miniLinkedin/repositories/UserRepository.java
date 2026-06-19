package com.example.miniLinkedin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.enums.Role;
import com.example.miniLinkedin.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
    // Authentification
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    // Activation du compte
    Optional<UserEntity> findByActivationToken(String token);

    // Administration
    List<UserEntity> findByRole(Role role);
    List<UserEntity> findByIsActive(boolean isActive);
    
    List<UserEntity> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String lastName, String email);
    
}
