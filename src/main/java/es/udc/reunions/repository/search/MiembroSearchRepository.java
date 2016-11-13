package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Miembro;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Miembro entity.
 */
public interface MiembroSearchRepository extends ElasticsearchRepository<Miembro, Long> {
}
