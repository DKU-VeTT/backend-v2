package kr.ac.dankook.VettObservabilityService.repository;

import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventDocumentRepository extends MongoRepository<EventDocument, String> {
    List<EventDocument> findByStatus(String status);
}
