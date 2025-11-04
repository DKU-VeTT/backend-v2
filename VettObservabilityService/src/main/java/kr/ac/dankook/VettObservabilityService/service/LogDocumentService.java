package kr.ac.dankook.VettObservabilityService.service;

import kr.ac.dankook.VettObservabilityService.document.LogDocument;
import kr.ac.dankook.VettObservabilityService.document.LogLevel;
import kr.ac.dankook.VettObservabilityService.dto.response.LogResponse;
import kr.ac.dankook.VettObservabilityService.repository.LogDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogDocumentService {

    private final LogDocumentRepository logDocumentRepository;

    public List<LogResponse> findByLevel(LogLevel level){
        List<LogDocument> logDocuments = logDocumentRepository.findByLevel(level.toString());
        return logDocuments.stream().map(LogResponse::new).toList();
    }
    public List<LogResponse> findByServiceName(String serviceName){
        List<LogDocument> logDocuments = logDocumentRepository.findByService(serviceName);
        return logDocuments.stream().map(LogResponse::new).toList();
    }
    public List<LogResponse> findByTraceId(String traceId){
        List<LogDocument> logDocuments = logDocumentRepository.findByTraceId(traceId);
        return logDocuments.stream().map(LogResponse::new).toList();
    }
}
