package es.udc.reunions.service.dto;

import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.enumeration.Asistencia;

/**
 * Created by diego on 3/12/16.
 */
public class LineaResumen {
    private final String curso;
    private final Organo organo;
    private int convocado, asiste, falta, disculpa;

    public LineaResumen(String curso, Organo organo) {
        this.disculpa = 0;
        this.falta = 0;
        this.asiste = 0;
        this.convocado = 0;
        this.curso = curso;
        this.organo = organo;
    }

    public void add(Asistencia asistencia) {
        switch (asistencia) {
            case asiste : this.asiste++;
                break;
            case disculpa : this.disculpa++;
                break;
            case falta : this.falta++;
                break;
        }
        this.convocado++;
    }

    public String getCurso() {
        return curso;
    }

    public Organo getOrgano() {
        return organo;
    }

    public int getConvocado() {
        return convocado;
    }

    public int getAsiste() {
        return asiste;
    }

    public int getFalta() {
        return falta;
    }

    public int getDisculpa() {
        return disculpa;
    }

}
