package kr.ac.dankook.VettObservabilityService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import kr.ac.dankook.VettObservabilityService.document.EventStatus;
import kr.ac.dankook.VettObservabilityService.dto.request.UpdateEventRequest;
import kr.ac.dankook.VettObservabilityService.dto.response.EventResponse;
import kr.ac.dankook.VettObservabilityService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettObservabilityService.event.FailedEventRePublisher;
import kr.ac.dankook.VettObservabilityService.log.LogMessage;
import kr.ac.dankook.VettObservabilityService.repository.EventDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDocumentService {

    private final EventDocumentRepository eventDocumentRepository;
    private final FailedEventRePublisher failedEventRePublisher;
    private final ObjectMapper objectMapper;

    public List<EventResponse> getAllFailedEventList(){
        return eventDocumentRepository.findAll().stream().map(EventResponse::new).toList();
    }

    public List<EventResponse> getFailedEventByStatus(EventStatus status){
        return eventDocumentRepository.findByStatus(status.toString())
                .stream().map(EventResponse::new).toList();
    }

    public void updateFailedEvent(String id, UpdateEventRequest req){
        EventDocument eventDocument = eventDocumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));
        String payload = null;
        try{
            payload = objectMapper.writeValueAsString(req.getPayload());
        }catch (JsonProcessingException e){
            log.error("{}, CLASS={}, METHOD={}, PAYLOAD={}, ERROR={}",
                    LogMessage.JSON_PROCESSING_ERROR, "EventDocumentService", "updateFailedEvent", req.getPayload(),
                    e.getMessage());
        }
        eventDocument.updateEvent(req.getTopic(),req.getPartitionKey(),payload, req.getStatus());
        eventDocumentRepository.save(eventDocument);
    }

    public void retryPublishEvent(String id){

        EventDocument eventDocument = eventDocumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));

        String topic = eventDocument.getTopic();
        String partitionKey = eventDocument.getPartitionKey();
        String payload = eventDocument.getPayload();

        failedEventRePublisher.retryPublishEvent(eventDocument, topic, partitionKey, payload);
    }

    public void deleteEvent(String id){
        eventDocumentRepository.deleteById(id);
    }
}
