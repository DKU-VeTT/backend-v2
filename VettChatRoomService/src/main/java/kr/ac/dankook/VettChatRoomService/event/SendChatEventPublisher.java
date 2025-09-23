package kr.ac.dankook.VettChatRoomService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatRoomService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatRoomService.entity.OutboxEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendChatEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExitAndEnterMessage(ChatMessageRequest chatMessageRequest){

        String payload;
        String topic = OutboxEventType.CHAT_ROOM_MESSAGE.getEventType();
        try{
            payload = objectMapper.writeValueAsString(chatMessageRequest);
        }catch (JsonProcessingException e){
            log.error("[Exit and enter message converting error] Error during convert to string - {}",e.getMessage());
            return;
        }
        CompletableFuture<SendResult<String,String>> future =
                kafkaTemplate.send(topic,payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Chat room event sent successfully. topic={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send chat room event: {}", ex.getMessage(), ex);
            }
        });
    }
}
