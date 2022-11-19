package de.ostfalia.bips.ws22.camunda;

import de.ostfalia.bips.ws22.camunda.database.domain.Keyword;
import de.ostfalia.bips.ws22.camunda.database.service.KeywordService;
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

    private final KeywordService keywordService;
    private final SupervisorService supervisorService;

    public Worker(KeywordService keywordService,
                  SupervisorService supervisorService) {
        this.keywordService = keywordService;
        this.supervisorService = supervisorService;
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

    @ZeebeWorker(type = "load-keywords", autoComplete = true)
    public Map<String, Object> loadKeywords(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Load Keywords");

        final List<Option<Integer>> keywords = keywordService.getRepository().findAll().stream()
                .map(e -> new Option<>(e.getText(), e.getId()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("keywords", keywords);
        return variables;
    }

    @ZeebeWorker(type = "load-supervisors", autoComplete = true)
    public Map<String, Object> loadSupervisors(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Load supervisors");

        // Get the
        final Object keyword = job.getVariablesAsMap().get("keyword");
        final Optional<Keyword> found;
        if (keyword instanceof Integer) {
            found = keywordService.getRepository().findById(((Integer) keyword));
        } else if (keyword != null && keyword.toString().matches("\\d+")) {
            found = keywordService.getRepository().findById(Integer.parseInt(keyword.toString()));
        } else {
            found = Optional.empty();
        }
        final List<Option<String>> supervisors = found
                .map(supervisorService::findAllByKeyword)
                .orElse(supervisorService.getRepository().findAll()).stream()
                .map(e -> new Option<>(e.getName(), e.getName()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("supervisors", supervisors);
        return variables;
    }
}
