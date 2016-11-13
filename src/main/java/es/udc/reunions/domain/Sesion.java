package es.udc.reunions.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Sesion.
 */
@Entity
@Table(name = "sesion")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sesion")
public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "numero", nullable = false)
    private Long numero;

    @NotNull
    @Column(name = "primera_convocatoria", nullable = false)
    private ZonedDateTime primeraConvocatoria;

    @Column(name = "segunda_convocatoria")
    private ZonedDateTime segundaConvocatoria;

    @NotNull
    @Column(name = "lugar", nullable = false)
    private String lugar;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @NotNull
    private Organo organo;

    @OneToMany(mappedBy = "sesion")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Participante> participantes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public Sesion numero(Long numero) {
        this.numero = numero;
        return this;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public ZonedDateTime getPrimeraConvocatoria() {
        return primeraConvocatoria;
    }

    public Sesion primeraConvocatoria(ZonedDateTime primeraConvocatoria) {
        this.primeraConvocatoria = primeraConvocatoria;
        return this;
    }

    public void setPrimeraConvocatoria(ZonedDateTime primeraConvocatoria) {
        this.primeraConvocatoria = primeraConvocatoria;
    }

    public ZonedDateTime getSegundaConvocatoria() {
        return segundaConvocatoria;
    }

    public Sesion segundaConvocatoria(ZonedDateTime segundaConvocatoria) {
        this.segundaConvocatoria = segundaConvocatoria;
        return this;
    }

    public void setSegundaConvocatoria(ZonedDateTime segundaConvocatoria) {
        this.segundaConvocatoria = segundaConvocatoria;
    }

    public String getLugar() {
        return lugar;
    }

    public Sesion lugar(String lugar) {
        this.lugar = lugar;
        return this;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Sesion descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Organo getOrgano() {
        return organo;
    }

    public Sesion organo(Organo organo) {
        this.organo = organo;
        return this;
    }

    public void setOrgano(Organo organo) {
        this.organo = organo;
    }

    public Set<Participante> getParticipantes() {
        return participantes;
    }

    public Sesion participantes(Set<Participante> participantes) {
        this.participantes = participantes;
        return this;
    }

    public Sesion addParticipante(Participante participante) {
        participantes.add(participante);
        participante.setSesion(this);
        return this;
    }

    public Sesion removeParticipante(Participante participante) {
        participantes.remove(participante);
        participante.setSesion(null);
        return this;
    }

    public void setParticipantes(Set<Participante> participantes) {
        this.participantes = participantes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sesion sesion = (Sesion) o;
        if(sesion.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sesion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sesion{" +
            "id=" + id +
            ", numero='" + numero + "'" +
            ", primeraConvocatoria='" + primeraConvocatoria + "'" +
            ", segundaConvocatoria='" + segundaConvocatoria + "'" +
            ", lugar='" + lugar + "'" +
            ", descripcion='" + descripcion + "'" +
            '}';
    }
}
