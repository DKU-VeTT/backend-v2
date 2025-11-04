package kr.ac.dankook.VettChatService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.entity.ChatRoomParticipant;
import kr.ac.dankook.VettChatService.facade.ChatRoomJoinFacade;
import kr.ac.dankook.VettChatService.log.LogMessage;
import kr.ac.dankook.VettChatService.repository.ChatRoomParticipantRepository;
import kr.ac.dankook.VettChatService.repository.EventRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberEventSubscriber {

    private final ObjectMapper objectMapper;
    private final EventRecordRepository eventRecordRepository;
    private final ChatRoomJoinFacade chatRoomJoinFacade;
    private final ChatRoomParticipantRepository participantRepository;

    @KafkaListener(groupId = "VETT_CHAT", topics = "user.event.deleted")
    @Transactional
    public void consumeMemberDeleted(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment ack) throws JsonProcessingException {

        OutboxEvent event = objectMapper.readValue(payload, OutboxEvent.class);

        String id = event.getId();
        if (eventRecordRepository.findById(id).isPresent()){
            log.info("{}, CLASS={}, METHOD={}, EVENT_ID={}",
                    LogMessage.MEMBER_EVENT_DUPLICATED_ACK_ONLY, "MemberEventSubscriber", "consumeMemberDeleted", id);
            ack.acknowledge();
            return;
        }
        eventRecordRepository.upsert(id);

        log.info("{}, CLASS={}, METHOD={}, TOPIC={}, PARTITION_KEY={}, PAYLOAD={}",
                LogMessage.MEMBER_EVENT_LISTENED, "MemberEventSubscriber", "consumeMemberDeleted",
                topic, partitionKey, payload);

        String memberId = event.getUserKey();
        List<ChatRoomParticipant> chatRooomList = participantRepository.findByMemberWithFetchJoin(memberId);
        chatRooomList.forEach(i -> {
            try {
                chatRoomJoinFacade.leaveChatRoom(i.getChatRoom().getId(),memberId);
            } catch (InterruptedException e) {
                log.error("{}, CLASS={}, METHOD={}, ERROR={}",
                        LogMessage.INTERRUPT_ERROR, "ChatRoomController", "joinChatRoom",
                        e.getMessage());
            }
        });
        ack.acknowledge();
    }
}
