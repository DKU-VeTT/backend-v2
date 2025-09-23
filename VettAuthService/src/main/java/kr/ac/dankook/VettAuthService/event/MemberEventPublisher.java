package kr.ac.dankook.VettAuthService.event;

import kr.ac.dankook.VettAuthService.entity.OutboxEvent;
import kr.ac.dankook.VettAuthService.entity.OutboxStatus;
import kr.ac.dankook.VettAuthService.service.MemberOutboxCacheService;
import kr.ac.dankook.VettAuthService.service.MemberOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.concurrent.CompletableFuture;
import static kr.ac.dankook.VettAuthService.service.MemberOutboxService.OUTBOX_TTL;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final MemberOutboxService memberOutboxService;
    private final MemberOutboxCacheService outboxCacheService;
    private final RetryTemplate retryTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePublishEvent(OutboxEvent outboxEvent){

        String id = outboxEvent.getId();
        String eventType = outboxEvent.getEventType();
        String payload = outboxEvent.getPayload();
        // redis check -> 이미 보내졌는지 확인
        if (outboxCacheService.checkIsAlreadyPublish(id)) return;
        CompletableFuture<SendResult<String,String>> future =
                kafkaTemplate.send(eventType,payload);

        future.whenComplete((result,ex) -> {
            if (ex == null){
                // Retry -> 3
                retryTemplate.execute(ctx -> {
                    // 성공 시 중복방지를 위한 TTL 설정
                    outboxCacheService.setOutboxId(id,OUTBOX_TTL);
                    // 성공 시 Published 상태로 변경
                    log.info("Save published status - {}",id);
                    memberOutboxService.convertOutboxStatus(id, OutboxStatus.PUBLISHED);
                    return null;
                });
            }else{
                log.error("Failed to send Kafka. eventId : {} / eventType : {}",id, eventType);
                retryTemplate.execute(ctx -> {
                    // 발행이 안되었으므로 Key를 삭제
                    outboxCacheService.deleteOutboxId(id);
                    // 실패로 변경
                    memberOutboxService.convertOutboxStatus(id, OutboxStatus.FAILED);
                    return null;
                });
            }
        });
    }
}
