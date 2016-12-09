package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Documento;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Documento entity.
 */
public interface DocumentoSearchRepository extends ElasticsearchRepository<Documento, Long> {
}
