package kr.ac.dankook.VettAuthService.event;

import jakarta.persistence.EntityManager;
import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxStatus;
import kr.ac.dankook.VettAuthService.log.LogMessage;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import kr.ac.dankook.VettAuthService.service.OutboxCacheService;
import kr.ac.dankook.VettAuthService.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventScheduling {

    private final OutboxRepository outboxRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final OutboxService outboxService;
    private final OutboxCacheService outboxCacheService;
    private final EntityManager entityManager;
    private final RetryTemplate retryTemplate;

    @Scheduled(fixedDelay = 60000 * 5) // 5 minute
    public void retryPublishEvent() {

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];
        log.info("[{}, class={}, method={}, date={}]",
                LogMessage.RETRY_PUBLISH_EVENT, className, methodName, LocalDateTime.now());


        List<Outbox> outboxes = outboxRepository.findByStatusIn(List.of(OutboxStatus.READY_TO_PUBLISH,OutboxStatus.FAILED));
        for (Outbox outbox : outboxes) {
            String id = outbox.getId();
            String eventType = outbox.getEventType();
            String payload = outbox.getPayload();
            String partitionKey = outbox.getPartitionKey();
            // 최대 3번 다시 시도
            retryTemplate.execute(ctx -> {
                outboxEventPublisher.publishOutboxEvent(id,eventType,partitionKey,payload);
                return null;
            }, ctx -> {
                log.error("[{}, class={}, method={}, id={}, attempts={}]",
                        LogMessage.RETRY_PUBLISH_EVENT_EXHAUSTED, className, methodName, id,ctx.getRetryCount());
                outboxCacheService.deleteOutboxId(id);
                outboxService.convertOutboxStatus(id, OutboxStatus.PERMANENT_FAILURE);
                return null;
            });
        }
    }

    @Scheduled(fixedDelay = 60000 * 10) // 10 minute
    @Transactional
    public void removePublishedEvents(){
        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];
        log.info("[{}, class={}, method={}, date={}]",
                LogMessage.REMOVE_PUBLISHED_EVENT, className, methodName, LocalDateTime.now());

        List<Outbox> outboxes = outboxRepository.findByStatusIn(List.of(OutboxStatus.PUBLISHED));
        Set<String> outboxIds = outboxes.stream().map(Outbox::getId).collect(Collectors.toSet());
        entityManager.flush();
        outboxRepository.deleteAllInBatch(outboxes);
        outboxCacheService.deleteOutboxId(outboxIds);
        entityManager.clear();
    }
}
