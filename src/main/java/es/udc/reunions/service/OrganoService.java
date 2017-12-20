package es.udc.reunions.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.reunions.domain.Organo;
import es.udc.reunions.repository.OrganoRepository;
import es.udc.reunions.repository.search.OrganoSearchRepository;

/**
 * Service Implementation for managing Organo.
 */
@Service
@Transactional
public class OrganoService {

	private final Logger log = LoggerFactory.getLogger(OrganoService.class);

	@Inject
	private OrganoRepository organoRepository;

	@Inject
	private OrganoSearchRepository organoSearchRepository;

	public OrganoService(OrganoRepository organoRepository, OrganoSearchRepository organoSearchRepository) {
		super();
		this.organoRepository = organoRepository;
		this.organoSearchRepository = organoSearchRepository;
	}

	public OrganoService() {
		super();
	}

	/**
	 * Save a organo.
	 *
	 * @param organo
	 *            the entity to save
	 * @return the persisted entity
	 */
	public Organo save(Organo organo) {
		log.debug("Request to save Organo : {}", organo);
		Organo result = organoRepository.save(organo);
		organoSearchRepository.save(result);
		return result;
	}

	/**
	 * Get all the organos.
	 *
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public List<Organo> findAll() {
		log.debug("Request to get all Organos");
		List<Organo> result = organoRepository.findAll();
		return result;
	}

	/**
	 * Get one organo by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public Organo findOne(Long id) {
		log.debug("Request to get Organo : {}", id);
		Organo organo = organoRepository.findOne(id);
		return organo;
	}

	/**
	 * Delete the organo by id.
	 *
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete Organo : {}", id);
		organoRepository.delete(id);
		organoSearchRepository.delete(id);
	}

	/**
	 * Search for the organo corresponding to the query.
	 *
	 * @param query
	 *            the query of the search
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<Organo> search(String query, Pageable pageable) {
		log.debug("Request to search for a page of Organos for query {}", query);
		Page<Organo> result = organoSearchRepository.search(queryStringQuery(query), pageable);
		return result;
	}
}
