package de.ostfalia.bips.ws22.camunda.database.service;

import java.util.List;
import de.ostfalia.bips.ws22.camunda.database.domain.Studierender;
import de.ostfalia.bips.ws22.camunda.database.repository.StichpunktRepository;
import de.ostfalia.bips.ws22.camunda.database.repository.StudierenderRepository;
import org.springframework.stereotype.Service;

@Service
public class StudierenderService {
    private final StudierenderRepository repository;

    public StudierenderService(StudierenderRepository repository) {
        this.repository = repository;
    }

    public StudierenderRepository getRepository() {
        return repository;
    }

    public Studierender save(Studierender studierender){
        studierender.setId(studierender.getId());
        studierender.setVorname(studierender.getVorname());
        studierender.setNachname(studierender.getNachname());
        studierender.setMailadresse(studierender.getMailadresse());
        return repository.save(studierender);
    }

    /*alle Schülerinformationen Iterierung*/
//    public List<Studierender> list(){
//        return studierenderREpository.findAll();
//    }
    /*add*/
//    public void Studierenderadd(Studierender studierender){
//        studierenderREpository.save(studierender);
//    }
}
