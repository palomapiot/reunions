package es.udc.reunions.repository;

import es.udc.reunions.domain.Documento;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Documento entity.
 */
@SuppressWarnings("unused")
public interface DocumentoRepository extends JpaRepository<Documento,Long> {

    List<Documento> findBySesionId(Long sesionId);
}
