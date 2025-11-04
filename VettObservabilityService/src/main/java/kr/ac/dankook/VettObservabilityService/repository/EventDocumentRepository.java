package kr.ac.dankook.VettObservabilityService.repository;

import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventDocumentRepository extends MongoRepository<EventDocument, String> {
}
