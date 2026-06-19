package com.example.miniLinkedin.repositories;

import com.example.miniLinkedin.entities.FormationEntity;
import com.example.miniLinkedin.entities.ProfilEntity;
import com.example.miniLinkedin.entities.UserEntity;
import com.example.miniLinkedin.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests DAO - FormationRepository")
class FormationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FormationRepository formationRepository;

    private ProfilEntity profil1;
    private ProfilEntity profil2;

    @BeforeEach
    void setUp() {
        UserEntity user1 = UserEntity.builder()
                .firstName("Alice").lastName("M").email("alice@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.PROFESSEUR).isActive(true).build();
        entityManager.persist(user1);

        UserEntity user2 = UserEntity.builder()
                .firstName("Bob").lastName("D").email("bob@test.com")
                .password("pwd").createdAt(LocalDateTime.now())
                .role(Role.ETUDIANT).isActive(true).build();
        entityManager.persist(user2);

        profil1 = ProfilEntity.builder()
                .name("Alice Martin").ville("Paris")
                .etablissement("Uni Paris").photoUrl("img.jpg").user(user1).build();
        entityManager.persist(profil1);

        profil2 = ProfilEntity.builder()
                .name("Bob Dupont").ville("Lyon")
                .etablissement("INSA Lyon").photoUrl("img.jpg").user(user2).build();
        entityManager.persist(profil2);

        // 2 formations pour profil1
        entityManager.persist(FormationEntity.builder()
                .diplome("Licence Informatique")
                .etablissement("Université Paris")
                .domaine("Informatique")
                .enCours(false)
                .dateDebut(LocalDate.of(2018, 9, 1))
                .dateFin(LocalDate.of(2021, 6, 30))
                .profil(profil1)
                .build());

        entityManager.persist(FormationEntity.builder()
                .diplome("Master Data Science")
                .etablissement("Université Paris-Saclay")
                .domaine("Data Science")
                .enCours(true)
                .dateDebut(LocalDate.of(2021, 9, 1))
                .dateFin(LocalDate.of(2023, 6, 30))
                .profil(profil1)
                .build());

        // 1 formation pour profil2
        entityManager.persist(FormationEntity.builder()
                .diplome("Ingénieur Informatique")
                .etablissement("INSA Lyon")
                .domaine("Génie Logiciel")
                .enCours(false)
                .dateDebut(LocalDate.of(2016, 9, 1))
                .dateFin(LocalDate.of(2021, 6, 30))
                .profil(profil2)
                .build());

        entityManager.flush();
    }

    // ── findByProfilId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByProfilId - doit retourner les formations du profil")
    void findByProfilId_shouldReturnFormationsForProfil() {
        List<FormationEntity> result = formationRepository.findByProfilId(profil1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FormationEntity::getDiplome)
                .containsExactlyInAnyOrder("Licence Informatique", "Master Data Science");
    }

    @Test
    @DisplayName("findByProfilId - doit retourner liste vide si aucune formation")
    void findByProfilId_shouldReturnEmpty_whenNoFormations() {
        List<FormationEntity> result = formationRepository.findByProfilId(9999L);

        assertThat(result).isEmpty();
    }

    // ── deleteByProfilId ──────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteByProfilId - doit supprimer toutes les formations du profil")
    void deleteByProfilId_shouldRemoveAllFormationsForProfil() {
        formationRepository.deleteByProfilId(profil1.getId());
        entityManager.flush();
        entityManager.clear();

        List<FormationEntity> remaining = formationRepository.findByProfilId(profil1.getId());

        assertThat(remaining).isEmpty();
    }

    @Test
    @DisplayName("deleteByProfilId - ne doit pas affecter les formations des autres profils")
    void deleteByProfilId_shouldNotAffectOtherProfils() {
        formationRepository.deleteByProfilId(profil1.getId());
        entityManager.flush();
        entityManager.clear();

        List<FormationEntity> remaining = formationRepository.findByProfilId(profil2.getId());

        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getDiplome()).isEqualTo("Ingénieur Informatique");
    }
}
