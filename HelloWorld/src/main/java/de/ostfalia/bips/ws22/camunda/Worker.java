package de.ostfalia.bips.ws22.camunda;

import de.ostfalia.bips.ws22.camunda.database.domain.*;
import de.ostfalia.bips.ws22.camunda.database.repository.ProfessorRepository;
import de.ostfalia.bips.ws22.camunda.database.service.*;
import de.ostfalia.bips.ws22.camunda.model.Option;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
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

    private final AbschlussarbeitService abschlussarbeitService;

    public Worker(StichpunktService stichpunktService,
                  ProfessorService professorService, StudierenderService studierenderService, AntragService antragService, AbschlussarbeitService abschlussarbeitService) {
        this.stichpunktService = stichpunktService;
        this.professorService = professorService;
        this.studierenderService = studierenderService;
        this.antragService = antragService;
        this.abschlussarbeitService = abschlussarbeitService;
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
        antrag.setGenehmigt_prof(-1);
        antrag.setGenehmigt_pav(0);
        antrag.setGenehmigt_ssb(0);
        antragService.getRepository().save(antrag);


        final List<Option<Integer>> antrags = antragService.getRepository().findallbyidprofessor(parseInt(profID.toString())).stream()
                .map(e -> new Option<>("Titel: "+e.getTitel()+",Studierender:"+e.getStudierender().getNachname()+",Studiumtyp:"+e.getTyp(),e.getId()))
                .collect(Collectors.toList());

        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("Antrag_Prof", antrags);
        return variables;
    }
    @ZeebeWorker(type = "Ergebnis_Pruefer", autoComplete = true)
    public Map<String, Object> profErgebnis(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Ergebnis von Prüfer in DB schreiben");
        final Object ergebnisProf= job.getVariablesAsMap().get("ErgebnisProf");
        final Object antragID=job.getVariablesAsMap().get("antragID");
        final Object grund=job.getVariablesAsMap().get("grund");


        if(parseInt(ergebnisProf.toString())==1){
            antragService.getRepository().setGenemigt_prof(parseInt(antragID.toString()),1);
        }else {
            antragService.getRepository().setGenemigt_prof(parseInt(antragID.toString()),0);
            antragService.getRepository().setBegruendung(parseInt(antragID.toString()),grund.toString());
        }

        final List<Option<Integer>> antrags = antragService.getRepository().findallByGenehmigt_prof().stream()
                .map(e -> new Option<>(e.getTitel(), e.getId()))
                .collect(Collectors.toList());
        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("Antrags", antrags);
        return variables;
    }

    @ZeebeWorker(type = "Ergebnis_PAV", autoComplete = true)
    public Map<String, Object> pavErgebnis(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Ergebnis von PAV in DB schreiben");
        final Object ergebnisPAV= job.getVariablesAsMap().get("ErgebnisPAV");
        final Object antragID=job.getVariablesAsMap().get("antragID");

        if(parseInt(ergebnisPAV.toString())==1){
            antragService.getRepository().setGenemigt_pav(parseInt(antragID.toString()),1);
        }

        final List<Option<Integer>> antragsPAV = antragService.getRepository().findallGenehmigt_pav().stream()
                .map(e -> new Option<>(e.getTitel(), e.getId()))
                .collect(Collectors.toList());
        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("AntragsPAV", antragsPAV);
        return variables;
    }

    @ZeebeWorker(type = "Ergebnis_SSB", autoComplete = true)
    public Map<String, Object> ssbErgebnis(final ActivatedJob job) {
        // Do the business logic
        LOGGER.info("Ergebnis von SSB in DB schreiben");
        final Object ergebnisSSB= job.getVariablesAsMap().get("ErgebnisSSB");
        final Object antragID=job.getVariablesAsMap().get("antragID");

        if(parseInt(ergebnisSSB.toString())==1){
            antragService.getRepository().setGenemigt_ssb(parseInt(antragID.toString()),1);
        }

        final List<Option<Integer>> antragsSSB = antragService.getRepository().findallGenehmigt_ssb().stream()
                .map(e -> new Option<>(e.getTitel(), e.getId()))
                .collect(Collectors.toList());
        // Probably add some process variables
        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("AntragsSSB", antragsSSB);
        return variables;
    }

    @ZeebeWorker(type = "Arbeit_anlegen", autoComplete = true)
    public Map<String, Object> Arbeitanlegen(final ActivatedJob job) throws ParseException {
        LOGGER.info("Arbeit formal angelegt wird, und das Ende_Datum gegeben wird.");

        final Object beginnDatum= job.getVariablesAsMap().get("beginnDatum");
        final Object antragID= job.getVariablesAsMap().get("antragID");
        final Object typ = job.getVariablesAsMap().get("studium_typ");


        Date ende_datum= new Date();
        SimpleDateFormat dateBformat = new SimpleDateFormat("yyyy/MM/dd");
        Date beginnDate=dateBformat.parse(beginnDatum.toString());
        System.out.println("beginnd: "+beginnDate);
        if(antragID!=null) {

            if (parseInt(typ.toString()) == 0) {
                Calendar bachlor = Calendar.getInstance();
                bachlor.setTime(beginnDate);
                bachlor.add(Calendar.DATE, 77);
                ende_datum = bachlor.getTime();
                System.out.println("enb: "+ende_datum);
                //enddate = stichpunktService.getRepository().findById(((Integer) sti));
            } else if (parseInt(typ.toString()) == 1) {
                Calendar master = Calendar.getInstance();
                master.setTime(beginnDate);
                master.add(Calendar.MONTH, 6);
                ende_datum = master.getTime();
                System.out.println("enm: "+ende_datum);
            }
        }

        final Abschlussarbeit abschlussarbeit=new Abschlussarbeit();
        final Antrag antrag=new Antrag();
        antrag.setId(parseInt(antragID.toString()));
        abschlussarbeit.setBeginn_datum(beginnDate);
        abschlussarbeit.setEnde_datum(ende_datum);
        abschlussarbeit.setAntrag(antrag);
        abschlussarbeitService.getRepository().save(abschlussarbeit);

        //endedatumberenen
        final List<Option<Integer>> arbeit = abschlussarbeitService.getRepository().findAll().stream()
                .map(e -> new Option<>( "Beginndatum: "+e.getBeginn_datum().toString()+" Enddatum: "+e.getEnde_datum().toString(), e.getIdAbscluss()))
                .collect(Collectors.toList());

        final HashMap<String, Object> variables = new HashMap<>();
        variables.put("arbeit", arbeit);
        return variables;
    }

}
