package es.udc.reunions.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.service.SesionService;
import es.udc.reunions.web.rest.util.HeaderUtil;
import es.udc.reunions.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * REST controller for managing Sesion.
 */
@RestController
@RequestMapping("/api")
public class SesionResource {

    private final Logger log = LoggerFactory.getLogger(SesionResource.class);
        
    @Inject
    private SesionService sesionService;

    /**
     * POST  /sesions : Create a new sesion.
     *
     * @param sesion the sesion to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sesion, or with status 400 (Bad Request) if the sesion has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sesions")
    @Timed
    public ResponseEntity<Sesion> createSesion(@Valid @RequestBody Sesion sesion) throws URISyntaxException {
        log.debug("REST request to save Sesion : {}", sesion);
        if (sesion.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "idexists", "A new sesion cannot already have an ID")).body(null);
        }
        Sesion result = sesionService.save(sesion);
        return ResponseEntity.created(new URI("/api/sesions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sesion", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sesions : Updates an existing sesion.
     *
     * @param sesion the sesion to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sesion,
     * or with status 400 (Bad Request) if the sesion is not valid,
     * or with status 500 (Internal Server Error) if the sesion couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sesions")
    @Timed
    public ResponseEntity<Sesion> updateSesion(@Valid @RequestBody Sesion sesion) throws URISyntaxException {
        log.debug("REST request to update Sesion : {}", sesion);
        if (sesion.getId() == null) {
            return createSesion(sesion);
        }
        Sesion result = sesionService.save(sesion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sesion", sesion.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sesions : get all the sesions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sesions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/sesions")
    @Timed
    public ResponseEntity<List<Sesion>> getAllSesions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sesions");
        Page<Sesion> page = sesionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sesions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sesions/:id : get the "id" sesion.
     *
     * @param id the id of the sesion to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sesion, or with status 404 (Not Found)
     */
    @GetMapping("/sesions/{id}")
    @Timed
    public ResponseEntity<Sesion> getSesion(@PathVariable Long id) {
        log.debug("REST request to get Sesion : {}", id);
        Sesion sesion = sesionService.findOne(id);
        return Optional.ofNullable(sesion)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sesions/:id : delete the "id" sesion.
     *
     * @param id the id of the sesion to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sesions/{id}")
    @Timed
    public ResponseEntity<Void> deleteSesion(@PathVariable Long id) {
        log.debug("REST request to delete Sesion : {}", id);
        sesionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sesion", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sesions?query=:query : search for the sesion corresponding
     * to the query.
     *
     * @param query the query of the sesion search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/sesions")
    @Timed
    public ResponseEntity<List<Sesion>> searchSesions(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Sesions for query {}", query);
        Page<Sesion> page = sesionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sesions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
