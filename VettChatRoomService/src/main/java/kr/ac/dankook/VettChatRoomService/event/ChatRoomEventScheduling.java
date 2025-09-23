package kr.ac.dankook.VettChatRoomService.event;

import jakarta.persistence.EntityManager;
import kr.ac.dankook.VettChatRoomService.entity.Outbox;
import kr.ac.dankook.VettChatRoomService.entity.OutboxEvent;
import kr.ac.dankook.VettChatRoomService.entity.OutboxStatus;
import kr.ac.dankook.VettChatRoomService.repository.OutboxRepository;
import kr.ac.dankook.VettChatRoomService.service.ChatRoomOutboxCacheService;
import kr.ac.dankook.VettChatRoomService.service.ChatRoomOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static kr.ac.dankook.VettChatRoomService.service.ChatRoomOutboxService.OUTBOX_TTL;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomEventScheduling {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ChatRoomOutboxService outboxService;
    private final ChatRoomOutboxCacheService outboxCacheService;
    private final EntityManager entityManager;
    private final RetryTemplate retryTemplate;

    // 실패 재시도는 동기로 처리
    @Scheduled(fixedDelay = 60000 * 3) // 3 minute
    public void retryPublishMessage() {
        log.info("Start retry publish message. Date : {}", LocalDateTime.now());
        List<Outbox> outboxes = outboxRepository.findByStatus(OutboxStatus.FAILED);

        for (Outbox outbox : outboxes) {
            OutboxEvent event = new OutboxEvent(outbox);
            String id = outbox.getId();
            String eventType = outbox.getEventType();
            String payload = outbox.getPayload();
            if (outboxCacheService.checkIsAlreadyPublish(id)) {
                log.info("Skip already published eventId={}", event.getId());
                continue;
            }
            // 최대 3번 다시 시도
            try{
                retryTemplate.execute(ctx -> {
                    kafkaTemplate.send(eventType, payload).get(10, TimeUnit.SECONDS);
                    outboxService.convertOutboxStatus(id, OutboxStatus.PUBLISHED);
                    try{
                        outboxCacheService.setOutboxId(id, OUTBOX_TTL);
                    }catch (Exception redisEr) {
                        log.warn("Redis set failed. eventId={}",id);
                    }
                    log.info("Successfully republished eventId={}", id);
                    return null;
                }, ctx -> {
                    Throwable last = ctx.getLastThrowable();
                    log.error("Retry exhausted ({} attempts). eventId={}",ctx.getRetryCount(),id,last);
                    outboxService.convertOutboxStatus(id, OutboxStatus.PERMANENT_FAILURE);
                    outboxCacheService.deleteOutboxId(id);
                    return null;
                });
            }catch (Exception e){
                log.error("Unexpected exception on retryTemplate.execute, eventId={}", id, e);
            }
        }
    }

    @Scheduled(fixedDelay = 60000 * 10) // 10 minute
    @Transactional
    public void removePublishedEvents(){
        log.info("Remove all published events. Date - {}",LocalDateTime.now());
        List<Outbox> outboxes = outboxRepository.findByStatus(OutboxStatus.PUBLISHED);
        Set<String> outboxIds = outboxes.stream().map(Outbox::getId).collect(Collectors.toSet());
        entityManager.flush();
        outboxRepository.deleteAllInBatch(outboxes);
        outboxCacheService.deleteOutboxId(outboxIds);
        entityManager.clear();
    }
}
