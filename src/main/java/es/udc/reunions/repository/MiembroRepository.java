package es.udc.reunions.repository;

import es.udc.reunions.domain.Miembro;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Miembro entity.
 */
@SuppressWarnings("unused")
public interface MiembroRepository extends JpaRepository<Miembro,Long> {

    @Query("select miembro from Miembro miembro where miembro.user.login = ?#{principal.username}")
    List<Miembro> findByUserIsCurrentUser();

}
