package kr.ac.dankook.VettObservabilityService.event;

import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import kr.ac.dankook.VettObservabilityService.document.EventStatus;
import kr.ac.dankook.VettObservabilityService.log.LogMessage;
import kr.ac.dankook.VettObservabilityService.repository.EventDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedEventSubscriber {

    private final EventDocumentRepository eventDocumentRepository;

    @KafkaListener(groupId = "VETT_OBSERVATION", topicPattern = ".*\\.dlt")
    public void consumeDltRecord(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header("service-name") String serviceName,
            @Header("original-topic") String originalTopic,
            @Header("error-class") String errorClass,
            @Header("error-message") String errorMessage,
            Acknowledgment ack){

        log.info("{}, CLASS={}, METHOD={}, SERVICE_NAME={}, ERROR_CLASS_NAME={}, ERROR={}, TOPIC={}, PARTITION_KEY={}, PAYLOAD={}",
                LogMessage.CONSUME_DLT_RECORD, "FailedEventSubscriber", "consumeDltRecord",
                serviceName, errorClass, errorMessage, originalTopic, partitionKey, payload);
        eventDocumentRepository.save(
                makeEventDocument(serviceName,errorClass,errorMessage,
                originalTopic,partitionKey,payload,EventStatus.CONSUME_FAILED));
        ack.acknowledge();
    }

    @KafkaListener(groupId = "VETT_OBSERVATION", topicPattern = ".*\\.pdlt")
    public void consumeFailPublishRecord(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header("service-name") String serviceName,
            @Header("original-topic") String originalTopic,
            @Header("error-class") String errorClass,
            @Header("error-message") String errorMessage,
            Acknowledgment ack) {

        log.info("{}, CLASS={}, METHOD={}, SERVICE_NAME={}, ERROR_CLASS_NAME={}, ERROR={}, TOPIC={}, PARTITION_KEY={}, PAYLOAD={}",
                LogMessage.CONSUME_PDLT_RECORD, "FailedEventSubscriber", "consumeFailPublishRecord",
                serviceName,errorClass,errorMessage,originalTopic,partitionKey, payload);
        eventDocumentRepository.save(
                makeEventDocument(serviceName,errorClass,errorMessage,
                originalTopic,partitionKey,payload,EventStatus.PUBLISH_FAILED));
        ack.acknowledge();
    }

    private EventDocument makeEventDocument(String serviceName, String errorClass, String errorMessage,
                                            String topic, String partitionKey, String payload, EventStatus status){
        return EventDocument
                .builder().serviceName(serviceName).errorClass(errorClass).errorMessage(errorMessage)
                .topic(topic).partitionKey(partitionKey).payload(payload).status(status).build();
    }
}
