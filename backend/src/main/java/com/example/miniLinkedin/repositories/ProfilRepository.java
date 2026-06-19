package com.example.miniLinkedin.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miniLinkedin.entities.ProfilEntity;
@Repository

public interface ProfilRepository extends JpaRepository<ProfilEntity, Long> {

    Optional<ProfilEntity> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
