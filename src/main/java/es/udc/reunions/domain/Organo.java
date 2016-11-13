package es.udc.reunions.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Organo.
 */
@Entity
@Table(name = "organo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "organo")
public class Organo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @NotNull
    private Grupo grupo;

    @OneToMany(mappedBy = "organo")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Miembro> miembros = new HashSet<>();

    @OneToMany(mappedBy = "organo")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Sesion> sesions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public Organo nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Organo descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public Organo grupo(Grupo grupo) {
        this.grupo = grupo;
        return this;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Set<Miembro> getMiembros() {
        return miembros;
    }

    public Organo miembros(Set<Miembro> miembros) {
        this.miembros = miembros;
        return this;
    }

    public Organo addMiembro(Miembro miembro) {
        miembros.add(miembro);
        miembro.setOrgano(this);
        return this;
    }

    public Organo removeMiembro(Miembro miembro) {
        miembros.remove(miembro);
        miembro.setOrgano(null);
        return this;
    }

    public void setMiembros(Set<Miembro> miembros) {
        this.miembros = miembros;
    }

    public Set<Sesion> getSesions() {
        return sesions;
    }

    public Organo sesions(Set<Sesion> sesions) {
        this.sesions = sesions;
        return this;
    }

    public Organo addSesion(Sesion sesion) {
        sesions.add(sesion);
        sesion.setOrgano(this);
        return this;
    }

    public Organo removeSesion(Sesion sesion) {
        sesions.remove(sesion);
        sesion.setOrgano(null);
        return this;
    }

    public void setSesions(Set<Sesion> sesions) {
        this.sesions = sesions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organo organo = (Organo) o;
        if(organo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, organo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Organo{" +
            "id=" + id +
            ", nombre='" + nombre + "'" +
            ", descripcion='" + descripcion + "'" +
            '}';
    }
}
