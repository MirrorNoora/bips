package de.ostfalia.bips.ws22.camunda.database.repository;

import de.ostfalia.bips.ws22.camunda.database.domain.Antrag;
import de.ostfalia.bips.ws22.camunda.database.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Date;
import java.util.List;
@Transactional
public interface AntragRepository extends JpaRepository<Antrag, Integer> {

    @Query(value = "select DISTINCT a from Antrag a where a.professor.id=:Professor and a.genehmigt_prof=-1")
    List<Antrag> findallbyidprofessor(@Param("Professor")Integer Professor);

    @Query(value = "select a from Antrag a where a.genehmigt_prof=1")
    List<Antrag> findallByGenehmigt_prof();

    @Query(value = "select a from Antrag a where a.genehmigt_prof=1 and a.genehmigt_pav=1")
    List<Antrag> findallGenehmigt_pav();

    @Query(value = "select a from Antrag a where a.genehmigt_prof=1 and a.genehmigt_pav=1 and a.genehmigt_ssb=1")
    List<Antrag> findallGenehmigt_ssb();

    @Modifying
    @Query(value = "update Antrag a set a.genehmigt_prof=:ErgebnisProf where a.id=:antragID")
    void setGenemigt_prof(@Param("antragID")Integer antragID,@Param("ErgebnisProf") Integer ergebnisProf);

    @Modifying
    @Query(value = "update Antrag a set a.genehmigt_pav=:ErgebnisPAV where a.id=:antragID")
    void setGenemigt_pav(@Param("antragID")Integer antragID,@Param("ErgebnisPAV") Integer ergebnisPAV);

    @Modifying
    @Query(value = "update Antrag a set a.genehmigt_ssb=:ErgebnisSSB where a.id=:antragID")
    void setGenemigt_ssb(@Param("antragID")Integer antragID,@Param("ErgebnisSSB") Integer ergebnisSSB);

    @Modifying
    @Query(value = "update Antrag a set a.begruendung_ablehnung=:grund where a.id=:antragID")
    void setBegruendung(@Param("antragID")Integer antragID,@Param("grund") String grund);

}
