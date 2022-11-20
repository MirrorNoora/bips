package de.ostfalia.bips.ws22.camunda.database.repository;

import de.ostfalia.bips.ws22.camunda.database.domain.Stichpunkt;
import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupervisorRepository extends JpaRepository<Professor, Integer> {
    @Query("SELECT DISTINCT s FROM Professor s " +
            "INNER JOIN ProfessorHatStichpunkt shk ON shk.id.professor = s " +
            "WHERE s.stichpunkt = :stichpunkt " +
            "OR shk.id.stichpunkt = :stichpunkt " +
            "ORDER BY s.vorname")
    List<Professor> findAllByKeyword(@Param("Stichpunkt")Stichpunkt stichpunkt);
}
