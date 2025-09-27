package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendChatEventSubscriber {

    private static final String CHAT_TOPIC = "chat.room.event.message";
    private final static String DESTINATION_PREFIX = "/sub/chat/room/";

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    // Have Three partitions
    @KafkaListener(groupId = "VETT_CHAT", topics = CHAT_TOPIC, concurrency = "3")
    public void handleReceiveChatMessage(String message, Acknowledgment acknowledgment) {
        try{
            ChatMessageRequest messageDto = objectMapper.readValue(message, ChatMessageRequest.class);
            messagingTemplate.convertAndSend(DESTINATION_PREFIX +  messageDto.getRoomId(), messageDto);
        }catch(Exception e){
            log.error("Json processing error during parsing message - {}",e.getMessage());
        }
        acknowledgment.acknowledge();
    }
}
