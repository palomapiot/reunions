package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Cargo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Cargo entity.
 */
public interface CargoSearchRepository extends ElasticsearchRepository<Cargo, Long> {
}
