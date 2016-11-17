package es.udc.reunions.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.service.OrganoService;
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
 * REST controller for managing Organo.
 */
@RestController
@RequestMapping("/api")
public class OrganoResource {

    private final Logger log = LoggerFactory.getLogger(OrganoResource.class);

    @Inject
    private OrganoService organoService;

    /**
     * POST  /organos : Create a new organo.
     *
     * @param organo the organo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organo, or with status 400 (Bad Request) if the organo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/organos")
    @Timed
    public ResponseEntity<Organo> createOrgano(@Valid @RequestBody Organo organo) throws URISyntaxException {
        log.debug("REST request to save Organo : {}", organo);
        if (organo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("organo", "idexists", "A new organo cannot already have an ID")).body(null);
        }
        Organo result = organoService.save(organo);
        return ResponseEntity.created(new URI("/api/organos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("organo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organos : Updates an existing organo.
     *
     * @param organo the organo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organo,
     * or with status 400 (Bad Request) if the organo is not valid,
     * or with status 500 (Internal Server Error) if the organo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/organos")
    @Timed
    public ResponseEntity<Organo> updateOrgano(@Valid @RequestBody Organo organo) throws URISyntaxException {
        log.debug("REST request to update Organo : {}", organo);
        if (organo.getId() == null) {
            return createOrgano(organo);
        }
        Organo result = organoService.save(organo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("organo", organo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /organos : get all the organos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of organos in body
     */
    @GetMapping("/organos")
    @Timed
    public ResponseEntity<List<Organo>> getAllOrganos() {
        log.debug("REST request to get a page of Organos");
        List<Organo> organos = organoService.findAll();
        return new ResponseEntity<>(organos, HttpStatus.OK);
    }

    /**
     * GET  /organos/:id : get the "id" organo.
     *
     * @param id the id of the organo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the organo, or with status 404 (Not Found)
     */
    @GetMapping("/organos/{id}")
    @Timed
    public ResponseEntity<Organo> getOrgano(@PathVariable Long id) {
        log.debug("REST request to get Organo : {}", id);
        Organo organo = organoService.findOne(id);
        return Optional.ofNullable(organo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /organos/:id : delete the "id" organo.
     *
     * @param id the id of the organo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/organos/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrgano(@PathVariable Long id) {
        log.debug("REST request to delete Organo : {}", id);
        organoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("organo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/organos?query=:query : search for the organo corresponding
     * to the query.
     *
     * @param query the query of the organo search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/organos")
    @Timed
    public ResponseEntity<List<Organo>> searchOrganos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Organos for query {}", query);
        Page<Organo> page = organoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/organos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
