package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendChatEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendChatMessage(ChatMessageRequest request){

        String payload;
        String topic = EventType.CHAT_ROOM_MESSAGE.getEventType();
        String roomKey = request.getRoomId();
        try{
            payload = objectMapper.writeValueAsString(request);
        }catch (JsonProcessingException e){
            log.error("Error during parsing messages - {}, {}",request.getMessage(),e.getMessage()) ;
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
        // Key를 RoomId로 설정하여 같은 파티션 내에서는 메시지 순서를 보장하도록 한다.
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic,roomKey,payload);
        future.whenComplete((result,ex) -> {
            if (ex != null){
                log.error("Kafka send exception - {}",ex.getMessage());
            }else{
                log.info("KafkaProducer - kafka send successful.");
            }
        });
    }
}
