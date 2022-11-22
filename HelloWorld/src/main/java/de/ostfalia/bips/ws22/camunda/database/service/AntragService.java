package de.ostfalia.bips.ws22.camunda.database.service;

import de.ostfalia.bips.ws22.camunda.database.domain.Antrag;
import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import de.ostfalia.bips.ws22.camunda.database.domain.Stichpunkt;
import de.ostfalia.bips.ws22.camunda.database.repository.AntragRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AntragService {
    private final AntragRepository repository;
    private Object Antrag;

    public AntragService(AntragRepository repository) {
        this.repository = repository;
    }

    public AntragRepository getRepository() {
        return repository;
    }

    public List<Antrag> findAllByidantrag(Antrag antrag) {
        if (antrag== null) {
            return Collections.emptyList();
        }
        return repository.findAllByidantrag(antrag);
    }

    public Antrag save(Antrag antrag){
        antrag.setId(antrag.getId());
        antrag.setStudierender(antrag.getStudierender());
        antrag.setProfessor(antrag.getProfessor());
        antrag.setTitel(antrag.getTitel());
        antrag.setTyp(antrag.getTyp(typ.toString()));
        antrag.setGenehmigt_prof(antrag.getGenehmigt_prof());
        antrag.setGenehmigt_pav(antrag.getGenehmigt_pav());
        antrag.setGenehmigt_ssb(antrag.getGenehmigt_ssb());
        antrag.setBegruendung_ablehnung(antrag.getBegruendung_ablehnung());
        return repository.save(antrag);
    }

}
