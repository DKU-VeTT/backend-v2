package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

import static kr.ac.dankook.VettChatService.event.ChatEventSubscriber.CHAT_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final FailedEventPublisher failedEventPublisher;
    private final ObjectMapper objectMapper;

    public void sendChatMessage(ChatMessageRequest request){

        String payload;
        String roomKey = request.getRoomId();
        String userKey = request.getMemberId();
        String message = request.getMessage();

        try{
            payload = objectMapper.writeValueAsString(request);
        }catch (JsonProcessingException e){
            log.error("{}, CLASS={}, METHOD={}, ERROR={}",
                    LogMessage.JSON_PROCESSING_ERROR, "ChatEventPublisher", "sendChatMessage", e.getMessage());
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(CHAT_TOPIC,roomKey,payload);

        future.whenComplete((result,ex) -> {
            if (ex != null){
                log.error("{}, CLASS={}, METHOD={}, USER_KEY={}, ROOM_KEY={}, MESSAGE={}, ERROR={}",LogMessage.KAFKA_SEND_EXCEPTION,
                        "ChatEventPublisher", "sendChatMessage", userKey, roomKey,message, ex.getMessage());
                failedEventPublisher.sendFailedEvent(
                        CHAT_TOPIC,"ChatEventPublisher", roomKey, payload, ex.getMessage());
            }
        });
    }
}
