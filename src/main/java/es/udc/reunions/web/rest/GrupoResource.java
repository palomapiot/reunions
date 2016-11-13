package es.udc.reunions.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.udc.reunions.domain.Grupo;

import es.udc.reunions.repository.GrupoRepository;
import es.udc.reunions.repository.search.GrupoSearchRepository;
import es.udc.reunions.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Grupo.
 */
@RestController
@RequestMapping("/api")
public class GrupoResource {

    private final Logger log = LoggerFactory.getLogger(GrupoResource.class);
        
    @Inject
    private GrupoRepository grupoRepository;

    @Inject
    private GrupoSearchRepository grupoSearchRepository;

    /**
     * POST  /grupos : Create a new grupo.
     *
     * @param grupo the grupo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new grupo, or with status 400 (Bad Request) if the grupo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/grupos")
    @Timed
    public ResponseEntity<Grupo> createGrupo(@Valid @RequestBody Grupo grupo) throws URISyntaxException {
        log.debug("REST request to save Grupo : {}", grupo);
        if (grupo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("grupo", "idexists", "A new grupo cannot already have an ID")).body(null);
        }
        Grupo result = grupoRepository.save(grupo);
        grupoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/grupos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("grupo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /grupos : Updates an existing grupo.
     *
     * @param grupo the grupo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated grupo,
     * or with status 400 (Bad Request) if the grupo is not valid,
     * or with status 500 (Internal Server Error) if the grupo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/grupos")
    @Timed
    public ResponseEntity<Grupo> updateGrupo(@Valid @RequestBody Grupo grupo) throws URISyntaxException {
        log.debug("REST request to update Grupo : {}", grupo);
        if (grupo.getId() == null) {
            return createGrupo(grupo);
        }
        Grupo result = grupoRepository.save(grupo);
        grupoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("grupo", grupo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /grupos : get all the grupos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of grupos in body
     */
    @GetMapping("/grupos")
    @Timed
    public List<Grupo> getAllGrupos() {
        log.debug("REST request to get all Grupos");
        List<Grupo> grupos = grupoRepository.findAll();
        return grupos;
    }

    /**
     * GET  /grupos/:id : get the "id" grupo.
     *
     * @param id the id of the grupo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the grupo, or with status 404 (Not Found)
     */
    @GetMapping("/grupos/{id}")
    @Timed
    public ResponseEntity<Grupo> getGrupo(@PathVariable Long id) {
        log.debug("REST request to get Grupo : {}", id);
        Grupo grupo = grupoRepository.findOne(id);
        return Optional.ofNullable(grupo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /grupos/:id : delete the "id" grupo.
     *
     * @param id the id of the grupo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/grupos/{id}")
    @Timed
    public ResponseEntity<Void> deleteGrupo(@PathVariable Long id) {
        log.debug("REST request to delete Grupo : {}", id);
        grupoRepository.delete(id);
        grupoSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("grupo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/grupos?query=:query : search for the grupo corresponding
     * to the query.
     *
     * @param query the query of the grupo search 
     * @return the result of the search
     */
    @GetMapping("/_search/grupos")
    @Timed
    public List<Grupo> searchGrupos(@RequestParam String query) {
        log.debug("REST request to search Grupos for query {}", query);
        return StreamSupport
            .stream(grupoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
