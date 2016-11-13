package es.udc.reunions.repository;

import es.udc.reunions.domain.Sesion;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Sesion entity.
 */
@SuppressWarnings("unused")
public interface SesionRepository extends JpaRepository<Sesion,Long> {

}
