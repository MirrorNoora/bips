package de.ostfalia.bips.ws22.camunda.database.service;

import java.util.List;
import de.ostfalia.bips.ws22.camunda.database.domain.Studierender;
import de.ostfalia.bips.ws22.camunda.database.repository.StudierenderREpository;

public class StudierenderService {
    private StudierenderREpository studierenderREpository;

/*alle Schülerinformationen Iterierung*/
    public List<Studierender> list(){
        return studierenderREpository.findAll();
    }
/*add*/
    public void Studierenderadd(Studierender studierender){
        studierenderREpository.save(studierender);
    }

}
