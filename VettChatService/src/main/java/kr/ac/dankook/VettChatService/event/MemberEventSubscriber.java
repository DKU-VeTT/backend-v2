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

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberEventSubscriber {

    private final ObjectMapper objectMapper;
    private final EventRecordRepository eventRecordRepository;
    private final ChatRoomJoinFacade chatRoomJoinFacade;
    private final ChatRoomParticipantRepository participantRepository;

    // JsonProcessing Exception or serialized or deserialize -> 바로 dlt로 이동
    // 다른 RuntimeError -> 5회 재시도 후 dlt로 이동
    @KafkaListener(groupId = "VETT_CHAT", topics = "user.event.deleted")
    @Transactional
    public void consumeMemberDeleted(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment ack) throws JsonProcessingException {

        OutboxEvent event = objectMapper.readValue(payload, OutboxEvent.class);

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        String id = event.getId();
        if (eventRecordRepository.findById(id).isPresent()){
            log.info("[{}, class={}, method={}, eventId={}]",
                    LogMessage.MEMBER_EVENT_DUPLICATED_ACK_ONLY, className, methodName, LocalDateTime.now());
            ack.acknowledge();
            return;
        }
        eventRecordRepository.upsert(id);

        log.info("[{}, class={}, method={}, topic={}, partitionKey={}, payload={}]",
                LogMessage.MEMBER_EVENT_LISTENED, className, methodName, topic, partitionKey, payload);
        // 비즈니스 로직
        String memberId = (String) event.getPayload().get("userKey");
        List<ChatRoomParticipant> chatRooomList = participantRepository.findByMemberWithFetchJoin(memberId);
        chatRooomList.forEach(i -> {
            try {
                chatRoomJoinFacade.leaveChatRoom(i.getChatRoom().getId(),memberId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        ack.acknowledge();
    }
}
