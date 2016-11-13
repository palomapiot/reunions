package es.udc.reunions.repository;

import es.udc.reunions.domain.Cargo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Cargo entity.
 */
@SuppressWarnings("unused")
public interface CargoRepository extends JpaRepository<Cargo,Long> {

}
