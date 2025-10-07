package kr.ac.dankook.VettAIRecordService.event;

import kr.ac.dankook.VettAIRecordService.entity.ChatBotRoom;
import kr.ac.dankook.VettAIRecordService.repository.ChatBotHistoryRepository;
import kr.ac.dankook.VettAIRecordService.repository.ChatBotRoomRepository;
import kr.ac.dankook.VettAIRecordService.util.EncryptionUtil;
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

    @KafkaListener(groupId = "VETT_AI_RECORD", topics = "user.event.deleted")
    public void consumeMemberDeleted(String message, Acknowledgment acknowledgment){

        log.info("[VETT_AI_RECORD] [Member Event Listener] UserDeleted. UserKey - {}",message);
        List<ChatBotRoom> chatBotRooms = chatBotRoomRepository.findChatBotRoomByOwnerId(message);
        for(ChatBotRoom room : chatBotRooms){
            chatBotHistoryRepository.deleteBySessionId(EncryptionUtil.encrypt(room.getId()));
            chatBotRoomRepository.delete(room);
        }
        acknowledgment.acknowledge();
    }
}
