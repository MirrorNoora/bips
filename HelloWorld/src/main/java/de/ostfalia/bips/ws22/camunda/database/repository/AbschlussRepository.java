package de.ostfalia.bips.ws22.camunda.database.repository;
import de.ostfalia.bips.ws22.camunda.database.domain.Abschlussarbeit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;


public interface AbschlussRepository extends JpaRepository<Abschlussarbeit, Integer> {

    @Query("select a.beginn_datum from Abschlussarbeit a where a.beginn_datum = :date")
    List<Abschlussarbeit> findAllbyDate(@Param("Datum") Date beginn_datum);


}
