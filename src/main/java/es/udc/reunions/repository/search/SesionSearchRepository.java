package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Sesion;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Sesion entity.
 */
public interface SesionSearchRepository extends ElasticsearchRepository<Sesion, Long> {
}
