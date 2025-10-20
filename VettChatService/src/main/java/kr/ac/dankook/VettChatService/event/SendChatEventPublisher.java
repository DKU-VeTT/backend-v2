package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static kr.ac.dankook.VettChatService.event.SendChatEventSubscriber.CHAT_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendChatEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendChatMessage(ChatMessageRequest request){

        String payload;
        String roomKey = request.getRoomId();
        String userKey = request.getMemberId();
        String message = request.getMessage();

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        try{
            payload = objectMapper.writeValueAsString(request);
        }catch (JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR,className,methodName,e.getMessage());
        }
        // Key를 RoomId로 설정하여 같은 파티션 내에서는 메시지 순서를 보장하도록 한다.
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(CHAT_TOPIC,roomKey,payload);
        future.whenComplete((result,ex) -> {
            if (ex != null){
                log.error(
                        "[{}, class={}, method={}, userKey={}, roomKey={}, message={}, error={}]",
                        LogMessage.KAFKA_SEND_EXCEPTION,className,methodName,userKey,roomKey,message, ex.getMessage());
            }
        });
    }
}
