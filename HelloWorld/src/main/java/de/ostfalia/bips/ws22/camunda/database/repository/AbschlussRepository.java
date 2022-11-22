package de.ostfalia.bips.ws22.camunda.database.repository;
import de.ostfalia.bips.ws22.camunda.database.domain.Abschlussarbeit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;


public interface AbschlussRepository extends JpaRepository<Abschlussarbeit, Integer> {




}
