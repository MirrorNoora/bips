package de.ostfalia.bips.ws22.camunda.database.service;

import de.ostfalia.bips.ws22.camunda.database.domain.Antrag;
import de.ostfalia.bips.ws22.camunda.database.repository.AntragRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class AntragService {
    private final AntragRepository repository;
    private Object Antrag;

    public AntragService(AntragRepository repository) {
        this.repository = repository;
    }

    public AntragRepository getRepository() {
        return repository;
    }

    public Antrag save(Antrag antrag){
        antrag.setId(antrag.getId());
        antrag.setStudierender(antrag.getStudierender());
        antrag.setProfessor(antrag.getProfessor());
        antrag.setTitel(antrag.getTitel());
        antrag.setTyp(antrag.getTyp());
        antrag.setGenehmigt_prof(antrag.getGenehmigt_prof());
        antrag.setGenehmigt_pav(antrag.getGenehmigt_pav());
        antrag.setGenehmigt_ssb(antrag.getGenehmigt_ssb());
        antrag.setBegruendung_ablehnung(antrag.getBegruendung_ablehnung());
        return repository.save(antrag);
    }

}
