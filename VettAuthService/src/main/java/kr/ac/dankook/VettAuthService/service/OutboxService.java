package kr.ac.dankook.VettAuthService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxEventType;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxStatus;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.error.exception.EntityNotFoundException;
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
    public static final long OUTBOX_TTL = 1000 * 60 * 30;

    private String makeOutboxPayload(String eventId, Map<String,String> payloadMap, OutboxEventType outboxEventType){

        Map<String, Object> payload = new HashMap<>();
        String eventDomain = outboxEventType.getEventDomain();
        String eventType = outboxEventType.getEventType();

        payload.put("id",eventId);
        payload.put("eventDomain",eventDomain);
        payload.put("eventType",eventType);
        payload.put("payload", payloadMap);
        try{
            return objectMapper.writeValueAsString(payload);
        }catch (JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }

    public Outbox makeOutbox(Map<String,String> payloadMap,OutboxEventType eventType, String partitionKey){

        String eventId = UUID.randomUUID().toString();
        String payload = makeOutboxPayload(eventId,payloadMap,eventType);
        return Outbox.builder()
                .id(eventId)
                .eventDomain(eventType.getEventDomain())
                .eventType(eventType.getEventType())
                .payload(payload)
                .partitionKey(partitionKey)
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
