package kr.ac.dankook.VettAIRecordService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettAIRecordService.entity.ChatBotRoom;
import kr.ac.dankook.VettAIRecordService.entity.Diagnosis;
import kr.ac.dankook.VettAIRecordService.facade.DiagnosisFacade;
import kr.ac.dankook.VettAIRecordService.repository.ChatBotRoomRepository;
import kr.ac.dankook.VettAIRecordService.repository.DiagnosisRepository;
import kr.ac.dankook.VettAIRecordService.repository.EventRecordRepository;
import kr.ac.dankook.VettAIRecordService.service.ChatBotRoomService;
import kr.ac.dankook.VettAIRecordService.util.EncryptionUtil;
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

    private final ChatBotRoomRepository chatBotRoomRepository;
    private final ChatBotRoomService chatBotRoomService;
    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisFacade diagnosisFacade;
    private final ObjectMapper objectMapper;
    private final EventRecordRepository eventRecordRepository;

    // JsonProcessing Exception or serialized or deserialize -> 바로 dlt로 이동
    // 다른 RuntimeError -> 5회 재시도 후 dlt로 이동
    @KafkaListener(groupId = "VETT_AI_RECORD", topics = "user.event.deleted")
    @Transactional
    public void consumeMemberDeleted(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment ack) throws JsonProcessingException {

        OutboxEvent event = objectMapper.readValue(payload, OutboxEvent.class);

        String id = event.getId();
        // 현재 Id로 이미 실행된 이벤트가 있는지 확인
        if (eventRecordRepository.findById(id).isPresent()){
            log.info("[AI RecordService] [Kafka Event Listener] Duplicate event {} -> ack only", id);
            ack.acknowledge();
            return;
        }
        // Upsert를 통해 행 삽입. PK 중복방지 + jpa id 수동 지정으로 인한 추가 Select 방지.
        // @Transactional로 인해 만약 비즈니스 로직 실패 시 해당 Record도 롤백되므로 재시도 가능 -> 중복 방지에 안걸리고 재시도 검증 완료
        // 따라서 현재 DB에 있는 레코드는 모두 해당 비즈니스 로직 성공이 완료되었음을 보장하게 된다. ( 아래 상황은 제외 )
        // -> Insert를 한 후 비즈니스 로직이 실행 완료 되어 커밋전까지는 제외 -> 이때는 레코드가 DB에 남아있지만 로직이 실행중이므로 완료 보장은 아니다.
        // 따라서 timestamp 기반으로 스케쥴러에서 해당 레코드를 삭제해야한다.
        eventRecordRepository.upsert(id);

        log.info("[AI RecordService] [Kafka Event Listener] topic {}, partitionKey {}, payload - {}", topic, partitionKey, payload);
        // 비즈니스 로직
        String ownerId = (String) event.getPayload().get("userKey");
        List<ChatBotRoom> chatBotRooms = chatBotRoomRepository.findChatBotRoomByOwnerId(ownerId);
        chatBotRooms.forEach(i -> chatBotRoomService.deleteChatBotRoom(EncryptionUtil.encrypt(i.getId())));

        List<Diagnosis> diagnoses = diagnosisRepository.findByMemberId(ownerId);
        diagnoses.forEach(i -> diagnosisFacade.deleteDiagnosisResult(i.getId()));

        ack.acknowledge();
    }
}
