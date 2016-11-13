package es.udc.reunions.repository;

import es.udc.reunions.domain.Grupo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Grupo entity.
 */
@SuppressWarnings("unused")
public interface GrupoRepository extends JpaRepository<Grupo,Long> {

}
