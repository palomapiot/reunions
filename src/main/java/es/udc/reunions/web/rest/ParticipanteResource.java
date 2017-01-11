package es.udc.reunions.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Participante;
import es.udc.reunions.security.AuthoritiesConstants;
import es.udc.reunions.security.SecurityUtils;
import es.udc.reunions.service.MiembroService;
import es.udc.reunions.service.ParticipanteService;
import es.udc.reunions.service.UserService;
import es.udc.reunions.web.rest.util.HeaderUtil;
import es.udc.reunions.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Participante.
 */
@RestController
@RequestMapping("/api")
public class ParticipanteResource {

    private final Logger log = LoggerFactory.getLogger(ParticipanteResource.class);

    @Inject
    private ParticipanteService participanteService;

    @Inject
    private MiembroService miembroService;

    @Inject
    private UserService userService;

    /**
     * POST  /participantes : Create a new participante.
     *
     * @param participante the participante to create
     * @return the ResponseEntity with status 201 (Created) and with body the new participante, or with status 400 (Bad Request) if the participante has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/participantes")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Participante> createParticipante(@Valid @RequestBody Participante participante) throws URISyntaxException {
        log.debug("REST request to save Participante : {}", participante);
        if (participante.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("participante", "idexists", "A new participante cannot already have an ID")).body(null);
        }
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(participante.getSesion().getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to create sessions")).body(null);
        }
        Participante result = participanteService.save(participante);
        return ResponseEntity.created(new URI("/api/participantes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("participante", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /participantes : Updates an existing participante.
     *
     * @param participante the participante to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated participante,
     * or with status 400 (Bad Request) if the participante is not valid,
     * or with status 500 (Internal Server Error) if the participante couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/participantes")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Participante> updateParticipante(@Valid @RequestBody Participante participante) throws URISyntaxException {
        log.debug("REST request to update Participante : {}", participante);
        if (participante.getId() == null) {
            return createParticipante(participante);
        }
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(participante.getSesion().getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to create sessions")).body(null);
        }
        Participante result = participanteService.save(participante);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("participante", participante.getId().toString()))
            .body(result);
    }

    /**
     * GET  /participantes : get all the participantes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of participantes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/participantes")
    @Timed
    public ResponseEntity<List<Participante>> getAllParticipantes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Participantes");
        Page<Participante> page = participanteService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/participantes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /participantes/:id : get the "id" participante.
     *
     * @param id the id of the participante to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the participante, or with status 404 (Not Found)
     */
    @GetMapping("/participantes/{id}")
    @Timed
    public ResponseEntity<Participante> getParticipante(@PathVariable Long id) {
        log.debug("REST request to get Participante : {}", id);
        Participante participante = participanteService.findOne(id);
        return Optional.ofNullable(participante)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /sesions/:id/participantes : get participantes from the "id" sesion.
     *
     * @param id the id of the sesion
     * @return the ResponseEntity with status 200 (OK) and the list of participantes in body
     */
    @GetMapping("/sesions/{id}/participantes")
    @Timed
    public ResponseEntity<List<Participante>> getParticipantesBySesionId( @PathVariable Long id) {
        log.debug("REST request to get participantes from sesion : {}", id);

        List<Participante> participantes = participanteService.findBySesionId(id);
        return new ResponseEntity<>(participantes, HttpStatus.OK);
    }

    /**
     * DELETE  /participantes/:id : delete the "id" participante.
     *
     * @param id the id of the participante to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/participantes/{id}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Void> deleteParticipante(@PathVariable Long id) {
        log.debug("REST request to delete Participante : {}", id);
        Participante participante = participanteService.findOne(id);
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(participante.getSesion().getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to create sessions")).body(null);
        }
        participanteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("participante", id.toString())).build();
    }

    /**
     * SEARCH  /_search/participantes?query=:query : search for the participante corresponding
     * to the query.
     *
     * @param query the query of the participante search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/participantes")
    @Timed
    public ResponseEntity<List<Participante>> searchParticipantes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Participantes for query {}", query);
        Page<Participante> page = participanteService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/participantes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
