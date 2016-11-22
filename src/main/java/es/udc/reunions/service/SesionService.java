package es.udc.reunions.service;

import es.udc.reunions.domain.Sesion;
import es.udc.reunions.repository.SesionRepository;
import es.udc.reunions.repository.search.SesionSearchRepository;
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
 * Service Implementation for managing Sesion.
 */
@Service
@Transactional
public class SesionService {

    private final Logger log = LoggerFactory.getLogger(SesionService.class);

    @Inject
    private SesionRepository sesionRepository;

    @Inject
    private SesionSearchRepository sesionSearchRepository;

    /**
     * Save a sesion.
     *
     * @param sesion the entity to save
     * @return the persisted entity
     */
    public Sesion save(Sesion sesion) {
        log.debug("Request to save Sesion : {}", sesion);
        Sesion result = sesionRepository.save(sesion);
        sesionSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the sesions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Sesion> findAll(Pageable pageable) {
        log.debug("Request to get all Sesions");
        Page<Sesion> result = sesionRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one sesion by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Sesion findOne(Long id) {
        log.debug("Request to get Sesion : {}", id);
        Sesion sesion = sesionRepository.findOne(id);
        return sesion;
    }

    /**
     *  Get all the sesiones from an organo
     *
     *  @param organoId the organo id
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Sesion> findByOrganoId(Long organoId) {
        log.debug("Request to get all miembros actuales from organo " + organoId);
        List<Sesion> result = sesionRepository.findByOrganoId(organoId);
        return result;
    }

    /**
     *  Get the last sesion for an organo
     *
     *  @param organoId the organo id
     *  @return the last sesion
     */
    @Transactional(readOnly = true)
    public Sesion lastSesion(Long organoId) {
        log.debug("Request to get last sesion for organo " + organoId);
        Sesion sesion = sesionRepository.findTopByOrganoIdOrderByNumeroDesc(organoId);
        return sesion;
    }

    /**
     *  Delete the  sesion by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Sesion : {}", id);
        sesionRepository.delete(id);
        sesionSearchRepository.delete(id);
    }

    /**
     * Search for the sesion corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Sesion> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sesions for query {}", query);
        Page<Sesion> result = sesionSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
