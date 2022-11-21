package de.ostfalia.bips.ws22.camunda.database.domain;

import javax.persistence.*;
import java.util.Objects;



@Entity
@Table(name = "studierender")
public class Studierender  {


    @Id
    @Column(name = "id_studierender", nullable = false)
    private Integer id;

    @Column(name = "mailadresse", nullable = false)
    private String mailadresse;

    @Column(name = "vorname", nullable = false)
    private String vorname;

    @Column(name = "nachname", nullable = false)
    private String nachname;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getMailadresse() {
        return mailadresse;
    }

    public void setMailadresse(String mailadresse) {
        this.mailadresse = mailadresse;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Studierender() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Studierender studierender = (Studierender) o;
        return Objects.equals(getId(), studierender.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Studierender{" +
                "id=" + id +
                ", mailadresse='" + mailadresse + '\'' +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                '}';
    }
}

