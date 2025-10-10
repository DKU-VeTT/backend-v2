package kr.ac.dankook.VettAuthService.event;

import kr.ac.dankook.VettAuthService.entity.outbox.OutboxStatus;
import kr.ac.dankook.VettAuthService.service.OutboxCacheService;
import kr.ac.dankook.VettAuthService.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static kr.ac.dankook.VettAuthService.service.OutboxService.OUTBOX_TTL;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final OutboxService outboxService;
    private final OutboxCacheService outboxCacheService;

    public void publishOutboxEvent(String id,String eventType,String partitionKey,String payload){

        // redis check -> 이미 보내졌는지 확인
        // Consumer 측에서 한 번 더 중복으로 처리했는지 체크 필요
        if (outboxCacheService.checkIsAlreadyPublish(id)) return;
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(eventType, partitionKey, payload);
        future.whenComplete((result,ex) -> {
            if (ex == null){
                outboxCacheService.setOutboxId(id,OUTBOX_TTL);
                outboxService.convertOutboxStatus(id, OutboxStatus.PUBLISHED);
                log.info("[Auth Service] Publish event - id : {}, type : {}, payload : {}",id,eventType,payload);
            }else{
                outboxCacheService.deleteOutboxId(id);
                outboxService.convertOutboxStatus(id, OutboxStatus.FAILED);
                log.error("[Auth Service] Failed to send event. - id : {}, type : {}, payload : {}",id,eventType,payload);
            }
        });
    }
}
