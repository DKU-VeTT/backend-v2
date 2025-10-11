package kr.ac.dankook.VettAIRecordService.event;

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
public class DltEventSubscriber {

    @KafkaListener(groupId = "VETT_AI_RECORD", topics = "user.event.deleted.dlt")
    public void consumeDtlRecord(@Payload String payload,
                                 @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 Acknowledgment ack){
        log.info("DLT Record {} {} {}",payload,partitionKey,topic);
        ack.acknowledge();
    }
}
