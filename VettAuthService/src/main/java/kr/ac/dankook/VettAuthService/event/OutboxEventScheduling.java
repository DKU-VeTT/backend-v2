package kr.ac.dankook.VettAuthService.event;

import jakarta.persistence.EntityManager;
import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxStatus;
import kr.ac.dankook.VettAuthService.log.LogMessage;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import kr.ac.dankook.VettAuthService.service.OutboxCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
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
    private final FailedEventPublisher failedEventPublisher;
    private final OutboxCacheService outboxCacheService;
    private final EntityManager entityManager;
    private final RetryTemplate retryTemplate;

    @Scheduled(fixedDelay = 60000 * 5) // 5 minute
    public void retryPublishEvent() {

        log.info("{}, CLASS={}, METHOD={}, DATE={}",
                LogMessage.RETRY_PUBLISH_EVENT, "OutboxEventScheduling", "retryPublishEvent",
                LocalDateTime.now());

        List<Outbox> outboxes = outboxRepository.findByStatusIn(
                List.of(OutboxStatus.READY_TO_PUBLISH,OutboxStatus.FAILED));
        for (Outbox outbox : outboxes) {
            String id = outbox.getId();
            String eventTopic = outbox.getEventTopic();
            String payload = outbox.getPayload();
            String partitionKey = outbox.getPartitionKey();
            retryTemplate.execute(ctx -> {
                outboxEventPublisher.publishOutboxEvent(id, eventTopic, partitionKey, payload);
                return null;
            }, ctx -> {
                Throwable lastError = ctx.getLastThrowable();
                String lastErrorMessage = lastError != null ? lastError.getMessage() : "Unknown Error";
                log.error("[{}, id={}, attempts={} lastError={}]",
                        LogMessage.RETRY_PUBLISH_EVENT_EXHAUSTED, id, ctx.getRetryCount(),
                        lastErrorMessage);
                outboxCacheService.deleteOutboxId(id);
                failedEventPublisher.sendFailedEvent(eventTopic,"OutboxEventScheduling", partitionKey, payload, lastErrorMessage);
                return null;
            });
        }
    }

    @Scheduled(fixedDelay = 60000 * 10)
    @Transactional
    public void removePublishedEvents(){

        log.info("{}, CLASS={}, METHOD={}, DATE={}",
                LogMessage.REMOVE_PUBLISHED_EVENT, "OutboxEventScheduling", "removePublishedEvents",
                LocalDateTime.now());

        List<Outbox> outboxes = outboxRepository.findByStatusIn(List.of(OutboxStatus.PUBLISHED));
        Set<String> outboxIds = outboxes.stream().map(Outbox::getId).collect(Collectors.toSet());
        entityManager.flush();
        outboxRepository.deleteAllInBatch(outboxes);
        outboxCacheService.deleteOutboxId(outboxIds);
        entityManager.clear();

    }
}
