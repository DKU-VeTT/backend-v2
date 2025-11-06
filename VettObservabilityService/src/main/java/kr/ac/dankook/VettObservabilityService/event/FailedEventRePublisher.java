package kr.ac.dankook.VettObservabilityService.event;

import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import kr.ac.dankook.VettObservabilityService.document.EventStatus;
import kr.ac.dankook.VettObservabilityService.log.LogMessage;
import kr.ac.dankook.VettObservabilityService.repository.EventDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedEventRePublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final EventDocumentRepository eventDocumentRepository;

    @Async
    public void retryPublishEvent(EventDocument eventDocument, String topic, String partitionKey, String payload){

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic,partitionKey,payload);

        future.whenComplete((result,ex) -> {
            if (ex == null){
                log.info("{}, CLASS={}, METHOD={}, ID={}, TOPIC={}, PARTITION_KEY={}, PAYLOAD={}", LogMessage.KAFKA_RETRY_SUCCESS,
                        "FailedEventRePublisher", "retryPublishEvent", eventDocument.getId(), topic, partitionKey, payload);
                eventDocument.convertStatus(EventStatus.RETRY_SUCCESS);
                eventDocumentRepository.save(eventDocument);
            }else{
                log.error("{}, CLASS={}, METHOD={}, ID={}, TOPIC={}, PARTITION_KEY={}, PAYLOAD={}, ERROR={}", LogMessage.KAFKA_SEND_EXCEPTION,
                        "FailedEventRePublisher", "retryPublishEvent", eventDocument.getId(), topic, partitionKey, payload, ex.getMessage());
            }
        });
    }
}
