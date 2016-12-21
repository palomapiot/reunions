package es.udc.reunions.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.security.AuthoritiesConstants;
import es.udc.reunions.service.MiembroService;
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
 * REST controller for managing Miembro.
 */
@RestController
@RequestMapping("/api")
public class MiembroResource {

    private final Logger log = LoggerFactory.getLogger(MiembroResource.class);

    @Inject
    private MiembroService miembroService;

    /**
     * POST  /miembros : Create a new miembro.
     *
     * @param miembro the miembro to create
     * @return the ResponseEntity with status 201 (Created) and with body the new miembro, or with status 400 (Bad Request) if the miembro has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/miembros")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Miembro> createMiembro(@Valid @RequestBody Miembro miembro) throws URISyntaxException {
        log.debug("REST request to save Miembro : {}", miembro);
        if (miembro.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("miembro", "idexists", "A new miembro cannot already have an ID")).body(null);
        }
        Miembro result = miembroService.save(miembro);
        return ResponseEntity.created(new URI("/api/miembros/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("miembro", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /miembros : Updates an existing miembro.
     *
     * @param miembro the miembro to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated miembro,
     * or with status 400 (Bad Request) if the miembro is not valid,
     * or with status 500 (Internal Server Error) if the miembro couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/miembros")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Miembro> updateMiembro(@Valid @RequestBody Miembro miembro) throws URISyntaxException {
        log.debug("REST request to update Miembro : {}", miembro);
        if (miembro.getId() == null) {
            return createMiembro(miembro);
        }
        Miembro result = miembroService.save(miembro);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("miembro", miembro.getId().toString()))
            .body(result);
    }

    /**
     * GET  /miembros : get all the miembros.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of miembros in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/miembros")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Miembro>> getAllMiembros(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Miembros");
        Page<Miembro> page = miembroService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/miembros");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /miembros/:id : get the "id" miembro.
     *
     * @param id the id of the miembro to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the miembro, or with status 404 (Not Found)
     */
    @GetMapping("/miembros/{id}")
    @Timed
    public ResponseEntity<Miembro> getMiembro(@PathVariable Long id) {
        log.debug("REST request to get Miembro : {}", id);
        Miembro miembro = miembroService.findOne(id);
        return Optional.ofNullable(miembro)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /organos/:id/miembros : get miembros from the "id" organo.
     *
     * @param id the id of the organo
     * @return the ResponseEntity with status 200 (OK) and the list of miembros in body
     */
    @GetMapping("/organos/{id}/miembros")
    @Timed
    public ResponseEntity<List<Miembro>> getMiembrosByOrganoIdAndFechaBajaIsNull( @PathVariable Long id) {
        log.debug("REST request to get miembros from organo : {}", id);

        List<Miembro> miembros = miembroService.findByOrganoIdAndFechaBajaIsNull(id);
        return new ResponseEntity<>(miembros, HttpStatus.OK);
    }

    /**
     * GET  /organos/:id/miembrosAnteriores : get miembros anteriores from the "id" organo.
     *
     * @param id the id of the organo
     * @return the ResponseEntity with status 200 (OK) and the list of miembros in body
     */
    @GetMapping("/organos/{id}/miembrosAnteriores")
    @Timed
    public ResponseEntity<List<Miembro>> getMiembrosByOrganoIdAndFechaBajaIsNotNull( @PathVariable Long id) {
        log.debug("REST request to get miembros anteriores from organo : {}", id);

        List<Miembro> miembros = miembroService.findByOrganoIdAndFechaBajaIsNotNull(id);
        return new ResponseEntity<>(miembros, HttpStatus.OK);
    }

    /**
     * DELETE  /miembros/:id : delete the "id" miembro.
     *
     * @param id the id of the miembro to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/miembros/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteMiembro(@PathVariable Long id) {
        log.debug("REST request to delete Miembro : {}", id);
        miembroService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("miembro", id.toString())).build();
    }

    /**
     * SEARCH  /_search/miembros?query=:query : search for the miembro corresponding
     * to the query.
     *
     * @param query the query of the miembro search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/miembros")
    @Timed
    public ResponseEntity<List<Miembro>> searchMiembros(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Miembros for query {}", query);
        Page<Miembro> page = miembroService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/miembros");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
