package de.ostfalia.bips.ws22.camunda.database.repository;

import de.ostfalia.bips.ws22.camunda.database.domain.Stichpunkt;
import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import de.ostfalia.bips.ws22.camunda.database.domain.ProfessorHatStichpunkt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.OrderBy;
import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Integer>{
    @SuppressWarnings("JpaQlInspection")
    @Query(value = "select DISTINCT p from Professor p INNER JOIN ProfessorHatStichpunkt phs ON phs.id.professor = p WHERE p.professor = :professor OR phs.id.stichpunkt = :stichpunkt ORDER BY p.vorname"
    )
    List<Professor> findAllByStichpunkt(@Param("Stichpunkt")Stichpunkt stichpunkt);//和super..service那边reposi引用
}
