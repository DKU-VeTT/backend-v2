package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import static kr.ac.dankook.VettChatService.config.kafka.KafkaConsumerConfig.SERVICE_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String DLT_SUFFIX = ".pdlt";

    @Async
    @Transactional
    public void sendFailedEvent(String topic, String className, String partitionKey, String payload, String errorMessage){

        ProducerRecord<String, String> record =
                new ProducerRecord<>(topic + DLT_SUFFIX, partitionKey, payload);

        record.headers().add("service-name", SERVICE_NAME.getBytes(StandardCharsets.UTF_8));
        record.headers().add("original-topic", topic.getBytes(StandardCharsets.UTF_8));
        record.headers().add("error-class", className.getBytes(StandardCharsets.UTF_8));
        record.headers().add("error-message", errorMessage.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
}
