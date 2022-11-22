package de.ostfalia.bips.ws22.camunda.database.repository;

import de.ostfalia.bips.ws22.camunda.database.domain.Abschlussarbeit;
import de.ostfalia.bips.ws22.camunda.database.domain.Antrag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sun.util.resources.cldr.en.CalendarData_en_UM;

import java.sql.Date;
import java.util.List;
public interface AntragRepository extends JpaRepository<Antrag, Integer> {

    @Query("select a from Antrag a " +
            "inner join Professor p ON p.id = a.professor.id " +
            "where p.id = a.professor.id")
    List<Antrag> findallbyidprofessor(@Param("Professor") Antrag antrag);

    @Query("select a from Antrag a " +
            "inner join Abschlussarbeit b ON b.antrag.id = a.id ")
    //select * from antrag
        //inner join abschlussarbeit ON abschlussarbeit.id_antrag = antrag.id_antrag;
    List<Antrag> findAllByidantrag(@Param("Typ")Antrag antrag);

}
