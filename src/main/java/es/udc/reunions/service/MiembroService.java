package es.udc.reunions.service;

import es.udc.reunions.domain.Miembro;
import es.udc.reunions.repository.MiembroRepository;
import es.udc.reunions.repository.search.MiembroSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Miembro.
 */
@Service
@Transactional
public class MiembroService {

    private final Logger log = LoggerFactory.getLogger(MiembroService.class);

    @Inject
    private MiembroRepository miembroRepository;

    @Inject
    private MiembroSearchRepository miembroSearchRepository;

    /**
     * Save a miembro.
     *
     * @param miembro the entity to save
     * @return the persisted entity
     */
    public Miembro save(Miembro miembro) {
        log.debug("Request to save Miembro : {}", miembro);
        Miembro result = miembroRepository.save(miembro);
        miembroSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the miembros.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Miembro> findAll(Pageable pageable) {
        log.debug("Request to get all Miembros");
        Page<Miembro> result = miembroRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get all the miembros from an organo
     *
     *  @param organoId the organo id
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Miembro> findByOrganoId(Long organoId, Pageable pageable) {
        log.debug("Request to get all Miembros from Organo " + organoId);
        Page<Miembro> result = miembroRepository.findByOrganoId(organoId, pageable);
        return result;
    }

    /**
     *  Get one miembro by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Miembro findOne(Long id) {
        log.debug("Request to get Miembro : {}", id);
        Miembro miembro = miembroRepository.findOne(id);
        return miembro;
    }

    /**
     *  Delete the  miembro by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Miembro : {}", id);
        miembroRepository.delete(id);
        miembroSearchRepository.delete(id);
    }

    /**
     * Search for the miembro corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Miembro> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Miembros for query {}", query);
        Page<Miembro> result = miembroSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
