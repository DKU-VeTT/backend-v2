package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("message", payload);

        FailedEvent failedEvent = FailedEvent.builder()
                .serviceName("CHAT")
                .className(className)
                .originalTopic(topic)
                .partitionKey(partitionKey)
                .errorMessage(errorMessage)
                .payload(payloadMap).build();
        try {
            String payloadString = objectMapper.writeValueAsString(failedEvent);
            kafkaTemplate.send(topic + DLT_SUFFIX, partitionKey, payloadString);
        } catch (JsonProcessingException e) {
            log.error("{}, CLASS={}, METHOD={}, ERROR={}",
                    LogMessage.JSON_PROCESSING_ERROR, "FailedEventPublisher", "sendFailedEvent", e.getMessage());
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }
}
