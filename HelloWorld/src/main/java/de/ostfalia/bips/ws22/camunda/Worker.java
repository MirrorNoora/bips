package de.ostfalia.bips.ws22.camunda;


import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import de.ostfalia.bips.ws22.camunda.database.domain.Stichpunkt;
import de.ostfalia.bips.ws22.camunda.database.service.KeywordService;
import de.ostfalia.bips.ws22.camunda.database.service.ProfessorService;
import de.ostfalia.bips.ws22.camunda.database.service.StichpunktService;
import de.ostfalia.bips.ws22.camunda.database.service.SupervisorService;
import de.ostfalia.bips.ws22.camunda.model.Option;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableZeebeClient
public class Worker {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    private final StichpunktService stichpunktService;

    private final ProfessorService professorService;

    public Worker(StichpunktService stichpunktService,
                  ProfessorService professorService) {
        this.stichpunktService = stichpunktService;
        this.professorService = professorService;
    }

    @ZeebeWorker(type = "hello-world", autoComplete = true)
    public Map<String, Object> helloWorld(final ActivatedJob job) {

        // Do the business logic
        LOGGER.info("Hello, World: " + job.getVariablesAsMap().get("username"));

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("hello-world", true);
        return variables;
    }

    @ZeebeWorker(type = "lade_Stichpunkte", autoComplete = true)
    public Map<String, Object> ladeStichpunkte(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Lade Stichpunkte");

        final List<Option<Integer>> stichpunkte = stichpunktService.getRepository().findAll().stream()
                .map(e -> new Option<>(e.getTitel(), e.getId()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("Stichpunkte", stichpunkte);
        return variables;
    }

    @ZeebeWorker(type = "suche_Betreuer", autoComplete = true)
    public Map<String, Object> sucheProfessor(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Suche passende Betreuer");

        // Get the
        final Object stichpunkte = job.getVariablesAsMap().get("stichpunkte");
        final Optional<Stichpunkt> found;
        if (stichpunkte instanceof Integer) {
            found = stichpunktService.getRepository().findById(((Integer) stichpunkte));
        } else if (stichpunkte != null && stichpunkte.toString().matches("\\d+")) {
            found = stichpunktService.getRepository().findById(Integer.parseInt(stichpunkte.toString()));
        } else {
            found = Optional.empty();
        }
        final List<Option<String>> professoren = found
                .map(professorService::findAllByStichpunkt)
                .orElse(professorService.getRepository().findAll()).stream()
                .map(e -> new Option<>(e.getVorname(), e.getNachname()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("Professoren", professoren);
        return variables;
    }
}
