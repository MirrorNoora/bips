package de.ostfalia.bips.ws22.camunda.database.service;

import de.ostfalia.bips.ws22.camunda.database.repository.AbschlussRepository;
import de.ostfalia.bips.ws22.camunda.database.repository.StichpunktRepository;
import org.springframework.stereotype.Service;

//Enddatum anhand Startdatum berechnen
//Absclussarbeit erstellen
@Service
public class AbschlussarbeitService {
    private final AbschlussRepository repository;

    public AbschlussarbeitService(AbschlussRepository repository) {
        this.repository = repository;
    }

    public AbschlussRepository getRepository() {
        return repository;
    }
}
