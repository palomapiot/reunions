package es.udc.reunions.service;

import es.udc.reunions.domain.Participante;
import es.udc.reunions.repository.ParticipanteRepository;
import es.udc.reunions.repository.search.ParticipanteSearchRepository;
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
 * Service Implementation for managing Participante.
 */
@Service
@Transactional
public class ParticipanteService {

    private final Logger log = LoggerFactory.getLogger(ParticipanteService.class);
    
    @Inject
    private ParticipanteRepository participanteRepository;

    @Inject
    private ParticipanteSearchRepository participanteSearchRepository;

    /**
     * Save a participante.
     *
     * @param participante the entity to save
     * @return the persisted entity
     */
    public Participante save(Participante participante) {
        log.debug("Request to save Participante : {}", participante);
        Participante result = participanteRepository.save(participante);
        participanteSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the participantes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Participante> findAll(Pageable pageable) {
        log.debug("Request to get all Participantes");
        Page<Participante> result = participanteRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one participante by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Participante findOne(Long id) {
        log.debug("Request to get Participante : {}", id);
        Participante participante = participanteRepository.findOne(id);
        return participante;
    }

    /**
     *  Delete the  participante by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Participante : {}", id);
        participanteRepository.delete(id);
        participanteSearchRepository.delete(id);
    }

    /**
     * Search for the participante corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Participante> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Participantes for query {}", query);
        Page<Participante> result = participanteSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}