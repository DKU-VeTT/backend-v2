package kr.ac.dankook.VettObservabilityService.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import kr.ac.dankook.VettObservabilityService.log.LogMessage;
import kr.ac.dankook.VettObservabilityService.repository.EventDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceEventSubscriber {

    private final ObjectMapper objectMapper;
    private final EventDocumentRepository eventDocumentRepository;

    @KafkaListener(groupId = "VETT_OBSERVATION", topics = "service.event.record")
    public void consumeServiceEventRecord(
                                 @Payload String payload,
                                 Acknowledgment ack){
        try{
            ServiceEvent serviceEvent = objectMapper.readValue(payload, ServiceEvent.class);
            EventDocument eventDocument = new EventDocument(serviceEvent);
            eventDocumentRepository.save(eventDocument);
        }catch (Exception e){
            log.error("[{}, class={}, method={}, payload={}, error={}]",
                    LogMessage.EVENT_CONSUME_ERROR, "ServiceEventSubscriber", "consumeServiceEventRecord", payload, e.getMessage());
        }
        ack.acknowledge();
    }
}
