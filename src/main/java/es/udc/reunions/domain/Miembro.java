package es.udc.reunions.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Miembro.
 */
@Entity
@Table(name = "miembro")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "miembro")
public class Miembro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne
    @NotNull
    private Organo organo;

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

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public Miembro fechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
        return this;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public Miembro fechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
        return this;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Miembro observaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Organo getOrgano() {
        return organo;
    }

    public Miembro organo(Organo organo) {
        this.organo = organo;
        return this;
    }

    public void setOrgano(Organo organo) {
        this.organo = organo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public Miembro cargo(Cargo cargo) {
        this.cargo = cargo;
        return this;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public User getUser() {
        return user;
    }

    public Miembro user(User user) {
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
        Miembro miembro = (Miembro) o;
        if(miembro.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, miembro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Miembro{" +
            "id=" + id +
            ", fechaAlta='" + fechaAlta + "'" +
            ", fechaBaja='" + fechaBaja + "'" +
            ", observaciones='" + observaciones + "'" +
            '}';
    }
}
