package kr.ac.dankook.VettAuthService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxEventType;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxStatus;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettAuthService.log.LogMessage;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    public static final long OUTBOX_TTL = 1000 * 60 * 60;

    public Outbox makeMemberOutbox(OutboxEventType eventType, String userKey){

        Map<String,Object> payloadMap = new HashMap<>();

        String eventId = UUID.randomUUID().toString();
        String eventDomain = eventType.getEventDomain();
        String eventTopic = eventType.getEventTopic();

        payloadMap.put("id", eventId);
        payloadMap.put("eventDomain", eventDomain);
        payloadMap.put("eventTopic", eventTopic);
        payloadMap.put("userKey", userKey);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(payloadMap);
        } catch (JsonProcessingException e) {
            log.error("{}, CLASS={}, METHOD={}, ERROR={}",
                    LogMessage.JSON_PROCESSING_ERROR, "OutboxService", "makeMemberOutbox",
                    e.getMessage());
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
        return Outbox.builder()
                .id(eventId)
                .eventDomain(eventType.getEventDomain())
                .eventTopic(eventType.getEventTopic())
                .payload(payload)
                .partitionKey(userKey)
                .status(OutboxStatus.READY_TO_PUBLISH)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void convertOutboxStatus(String id,OutboxStatus outboxStatus){
        Outbox outbox = outboxRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException("저장된 데이터가 존재하지 않습니다."));
        outbox.setStatus(outboxStatus);
        outboxRepository.save(outbox);
    }
}
