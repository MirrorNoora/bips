package de.ostfalia.bips.ws22.camunda.database.domain;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "professor")
public class Professor {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_professor", nullable = false)
private Integer id;

@Column(name = "vorname", nullable = false)
private String vorname;

@Column(name = "nachname", nullable = false)
private String nachname;

@Column(name = "titel", nullable = false)
private String titel;

@Column(name = "mailadresse", nullable = false)
private String mailadresse;

@ManyToOne(targetEntity = Keyword.class, optional = false)
@JoinColumn(name = "id_stichpunkt", referencedColumnName = "id_stichpunkt", nullable = false)
private Stichpunkt stichpunkt;

public Integer getId() {
        return id;
        }

public void setId(Integer id) {
        this.id = id;
        }

public String getVorname() {
        return name;
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

public String getTitel() {
        return titel;
        }

public void setTitel(String titel) {
        this.titel = titel;
        }

public String getMailadresse() {
        return mailadresse;
        }

public void setMailadresse(String mailadresse) {
        this.mailadresse =mailadresse;
        }

public Stichpunkt getStichpunkt() {
        return keyword;
        }

public void setStichpunkt(Stichpunkt stichpunkt) {
        this.stichpunkt = stichpunkt;
        }

@Override
public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supervisor that = (Supervisor) o;
        return Objects.equals(getId(), that.getId());
        }

@Override
public int hashCode() {
        return Objects.hash(getId());
        }

@Override
public String toString() {
        return name + " (" + (id == null ? "<null>" : id) + ')';
        }
        }

