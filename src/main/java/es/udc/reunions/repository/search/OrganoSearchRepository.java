package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Organo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Organo entity.
 */
public interface OrganoSearchRepository extends ElasticsearchRepository<Organo, Long> {
}
