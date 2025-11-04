package kr.ac.dankook.VettObservabilityService.repository;

import kr.ac.dankook.VettObservabilityService.document.LogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface LogDocumentRepository extends ElasticsearchRepository<LogDocument,String> {
    List<LogDocument> findByTraceId(String traceId);
    List<LogDocument> findByService(String service);
    List<LogDocument> findByLevel(String level);
}
