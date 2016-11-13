package es.udc.reunions.repository;

import es.udc.reunions.domain.Organo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Organo entity.
 */
@SuppressWarnings("unused")
public interface OrganoRepository extends JpaRepository<Organo,Long> {

}
