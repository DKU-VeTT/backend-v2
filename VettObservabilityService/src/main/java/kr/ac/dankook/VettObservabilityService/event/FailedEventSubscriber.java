package kr.ac.dankook.VettObservabilityService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    @KafkaListener(groupId = "VETT_OBSERVATION", topicPattern = ".*\\.dlt")
    public void consumeDTLRecord(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header("service-name") String serviceName,
            @Header("original-topic") String originalTopic,
            @Header("error-class") String errorClass,
            @Header("error-message") String errorMessage,
            Acknowledgment ack){

        log.info("DLT Record {} {} {}", payload,partitionKey,topic);
        log.info("Error Info {} {}",errorClass,errorMessage);
        ack.acknowledge();
    }

    @KafkaListener(groupId = "VETT_OBSERVATION", topicPattern = ".*\\.pdlt")
    public void consumeFailPublishRecord(
            @Payload String payload, Acknowledgment ack) {

        try{
            FailedEvent failedEvent = objectMapper.readValue(payload, FailedEvent.class);
            log.info("PDLT {} {} {} {} {} {}",failedEvent.getServiceName(),failedEvent.getClassName(),
                    failedEvent.getOriginalTopic(),
                    failedEvent.getPartitionKey(),failedEvent.getPayload().toString(), failedEvent.getErrorMessage());
        }catch (JsonProcessingException e){

        }
        ack.acknowledge();
    }
}
