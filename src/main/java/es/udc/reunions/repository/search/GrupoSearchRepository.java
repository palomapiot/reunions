package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Grupo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Grupo entity.
 */
public interface GrupoSearchRepository extends ElasticsearchRepository<Grupo, Long> {
}
