package de.ostfalia.bips.ws22.camunda.database.repository;
import de.ostfalia.bips.ws22.camunda.database.domain.Abschlussarbeit;
import de.ostfalia.bips.ws22.camunda.database.domain.Antrag;
import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import de.ostfalia.bips.ws22.camunda.database.domain.Stichpunkt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AbschlussRepository extends JpaRepository<Abschlussarbeit, String> {
    @Query(value = "")

    List<Abschlussarbeit> findAllByAntrag(@Param("antrag") Antrag antrag);
}

