package de.ostfalia.bips.ws22.camunda.database.repository;

import de.ostfalia.bips.ws22.camunda.database.domain.Studierender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudierenderREpository extends JpaRepository<Studierender, Integer> {
}
