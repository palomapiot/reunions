package es.udc.reunions.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import es.udc.reunions.domain.enumeration.Asistencia;

/**
 * A Participante.
 */
@Entity
@Table(name = "participante")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "participante")
public class Participante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "asistencia")
    private Asistencia asistencia;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne
    @NotNull
    private Sesion sesion;

    @ManyToOne
    @NotNull
    private Cargo cargo;

    @ManyToOne
    @NotNull
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asistencia getAsistencia() {
        return asistencia;
    }

    public Participante asistencia(Asistencia asistencia) {
        this.asistencia = asistencia;
        return this;
    }

    public void setAsistencia(Asistencia asistencia) {
        this.asistencia = asistencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Participante observaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public Participante sesion(Sesion sesion) {
        this.sesion = sesion;
        return this;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public Participante cargo(Cargo cargo) {
        this.cargo = cargo;
        return this;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public User getUser() {
        return user;
    }

    public Participante user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Participante participante = (Participante) o;
        if(participante.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, participante.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Participante{" +
            "id=" + id +
            ", asistencia='" + asistencia + "'" +
            ", observaciones='" + observaciones + "'" +
            '}';
    }
}
