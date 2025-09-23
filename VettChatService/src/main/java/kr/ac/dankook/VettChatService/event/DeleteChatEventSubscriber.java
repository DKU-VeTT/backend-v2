package kr.ac.dankook.VettChatService.event;

import kr.ac.dankook.VettChatService.entity.ChatMessage;
import kr.ac.dankook.VettChatService.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteChatEventSubscriber {

    private final ChatMessageRepository chatMessageRepository;
    private static final String CHAT_DELETE_TOPIC = "chat.room.event.deleted";

    @KafkaListener(groupId = "VETT_CHAT", topics = CHAT_DELETE_TOPIC)
    public void handleDeleteChatMessage(String message, Acknowledgment acknowledgment){
        log.info("DELETE SUB {}",message);
        List<ChatMessage> messages = chatMessageRepository.findByRoomId(message);
        chatMessageRepository.deleteAll(messages);
        acknowledgment.acknowledge();
    }
}
