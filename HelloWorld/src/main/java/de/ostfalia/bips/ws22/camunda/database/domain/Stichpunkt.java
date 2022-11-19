package de.ostfalia.bips.ws22.camunda.database.domain;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "stichpunkt")
public class Stichpunkt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stichpunkt", nullable = false)
    private Integer id;

    @Column(name = "titel", nullable = false)
    private String titel;

    @Column(name = "beschreibung", nullable = false)
    private String beschreibung;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stichpunkt stichpunkt = (Stichpunkt) o;
        return Objects.equals(getId(), stichpunkt.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return  titel + " (" + (id == null ? "<null>" : id) + ')';
    }
}
