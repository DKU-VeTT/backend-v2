package kr.ac.dankook.VettChatBotService.event;

import kr.ac.dankook.VettChatBotService.entity.ChatBotRoom;
import kr.ac.dankook.VettChatBotService.repository.ChatBotHistoryRepository;
import kr.ac.dankook.VettChatBotService.repository.ChatBotRoomRepository;
import kr.ac.dankook.VettChatBotService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberEventSubscriber {

    private final ChatBotRoomRepository chatBotRoomRepository;
    private final ChatBotHistoryRepository chatBotHistoryRepository;

    @KafkaListener(groupId = "VETT_CHATBOT", topics = "user.event.deleted")
    public void consumeMemberDeleted(String message, Acknowledgment acknowledgment){

        log.info("[VETT_CHATBOT] [Member Event Listener] UserDeleted. UserKey - {}",message);
        List<ChatBotRoom> chatBotRooms = chatBotRoomRepository.findChatBotRoomByOwnerId(message);
        for(ChatBotRoom room : chatBotRooms){
            chatBotHistoryRepository.deleteBySessionId(EncryptionUtil.encrypt(room.getId()));
            chatBotRoomRepository.delete(room);
        }
        acknowledgment.acknowledge();
    }

}
