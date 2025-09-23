package kr.ac.dankook.VettAuthService.service;

import kr.ac.dankook.VettAuthService.entity.*;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberOutboxService {

    private final OutboxRepository outboxRepository;
    public static final long OUTBOX_TTL = 1000 * 60 * 30;

    public Outbox makeOutbox(Member member, OutboxEventType eventType){

        String eventId = UUID.randomUUID().toString();
        String payload = EncryptionUtil.encrypt(member.getId());

        return Outbox.builder()
                .id(eventId).domain(eventType.getDomain())
                .eventType(eventType.getEventType())
                .payload(payload)
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
