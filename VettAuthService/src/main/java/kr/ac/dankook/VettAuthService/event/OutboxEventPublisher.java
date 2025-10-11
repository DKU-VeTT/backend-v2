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

        // Redis check -> 이미 보내졌는지 확인
        // 각 Random UUID에 대해 체크하는 것이므로 동시성 고려 X -> 분산락 SETNX X
        if (outboxCacheService.checkIsAlreadyPublish(id)) return;

        // 실제 메시지는 브로커에 이미 저장되었는데
        // outboxCacheService.setOutboxId()와
        // outboxService.convertOutboxStatus(PUBLISHED)가 아직 수행되지 않은 상태로 장애가 발생한다면?
        // Failed 이나 Ready 상태로 남아있을 수 있다. -> 캐시에 저장도 안되어 있음 -> 따라서 스케쥴러에 의해 2번 발행 가능
        // 이는 Consumer에서 upsert로 보장
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(eventType, partitionKey, payload);
        kafkaTemplate.send(eventType,partitionKey,payload);
        future.whenComplete((result,ex) -> {
            if (ex == null){
                outboxCacheService.setOutboxId(id,OUTBOX_TTL);
                // TTL이 설정은 수행되고 만약 해당 DB 저장 로직이 실패하면 Ready 상태로 남아있으므로
                // 캐시가 없다면 스케쥴러에서 중복 발행 가능성이 존재. -> 따라서 캐시가 필요
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
