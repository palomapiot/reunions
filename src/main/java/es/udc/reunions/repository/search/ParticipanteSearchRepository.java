package es.udc.reunions.repository.search;

import es.udc.reunions.domain.Participante;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Participante entity.
 */
public interface ParticipanteSearchRepository extends ElasticsearchRepository<Participante, Long> {
}
