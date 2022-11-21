package de.ostfalia.bips.ws22.camunda;

import de.ostfalia.bips.ws22.camunda.database.domain.Antrag;
import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import de.ostfalia.bips.ws22.camunda.database.domain.Stichpunkt;
import de.ostfalia.bips.ws22.camunda.database.domain.Studierender;
import de.ostfalia.bips.ws22.camunda.database.repository.ProfessorRepository;
import de.ostfalia.bips.ws22.camunda.database.service.AntragService;
import de.ostfalia.bips.ws22.camunda.database.service.ProfessorService;
import de.ostfalia.bips.ws22.camunda.database.service.StichpunktService;
import de.ostfalia.bips.ws22.camunda.database.service.StudierenderService;
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

import static java.lang.Integer.parseInt;

@SpringBootApplication
@EnableZeebeClient
public class Worker {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    private final StichpunktService stichpunktService;

    private final ProfessorService professorService;

    private final StudierenderService studierenderService;

    private final AntragService antragService;

    public Worker(StichpunktService stichpunktService,
                  ProfessorService professorService, StudierenderService studierenderService, AntragService antragService) {
        this.stichpunktService = stichpunktService;
        this.professorService = professorService;
        this.studierenderService = studierenderService;
        this.antragService = antragService;
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
        variables.put("stichpunkte", stichpunkte);
        return variables;
    }

    @ZeebeWorker(type = "suche_Betreuer", autoComplete = true)
    public Map<String, Object> sucheProfessor(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Suche passende Betreuer");
        ProfessorRepository s;

        //store students information
        final Object stuID= job.getVariablesAsMap().get("stu_id");
        final Object stuVorname= job.getVariablesAsMap().get("stu_vorname");
        final Object stuNachname= job.getVariablesAsMap().get("stu_nachname");
        final Object stuEmail= job.getVariablesAsMap().get("stu_email");
        final Studierender studierender=new Studierender();
        LOGGER.info("id:"+parseInt(stuID.toString())+ " Name:"+stuVorname.toString()+stuNachname.toString()+" email:"+stuEmail.toString());
        studierender.setId(parseInt(stuID.toString()));
        studierender.setVorname(stuVorname.toString());
        studierender.setNachname(stuNachname.toString());
        studierender.setMailadresse(stuEmail.toString());
        studierenderService.getRepository().save(studierender);

        // Get the Professoren
        final Object stichpunkt = job.getVariablesAsMap().get("stichpunkt");
        final Optional<Stichpunkt> found;
        if (stichpunkt instanceof Integer) {
            found = stichpunktService.getRepository().findById(((Integer) stichpunkt));
        } else if (stichpunkt != null && stichpunkt.toString().matches("\\d+")) {
            found = stichpunktService.getRepository().findById(parseInt(stichpunkt.toString()));
        } else {
            found = Optional.empty();
        }
        final List<Option<Integer>> professoren = found
                .map(professorService::findAllByStichpunkt)
                .orElse(professorService.getRepository().findAll()).stream()
                .map(e -> new Option<>(e.getTitel()+" "+e.getNachname(),e.getId()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("Professoren", professoren);
        return variables;
    }

    @ZeebeWorker(type = "DB_eintrag", autoComplete = true)
    public Map<String, Object> eintragDB(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Eintrag in DB anlegen");

        final Object stichpunkt = job.getVariablesAsMap().get("stichpunkt");
        final Object stuID= job.getVariablesAsMap().get("stu_id");
        final Object profID= job.getVariablesAsMap().get("Professor");
        final Object studiumTyp=job.getVariablesAsMap().get("studium_typ");
        final Antrag antrag=new Antrag();
        final Studierender studierender=new Studierender();
        studierender.setId(parseInt(stuID.toString()));
        final Professor prof=new Professor();
        prof.setId(parseInt(profID.toString()));
        antrag.setStudierender(studierender);
        antrag.setProfessor(prof);
        antrag.setTitel(stichpunktService.getRepository().findById(parseInt(stichpunkt.toString())).get().getTitel());
        antrag.setTyp(parseInt(studiumTyp.toString()));
        antrag.setGenehmigt_prof(0);
        antrag.setGenehmigt_pav(0);
        antrag.setGenehmigt_ssb(0);
        antragService.getRepository().save(antrag);

        final List<Option<Integer>> eintrag = antragService.getRepository().findAll().stream()
                .map(e -> new Option<>(e.getTitel(), e.getId()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("Antrag", eintrag);
        return variables;
    }
}
