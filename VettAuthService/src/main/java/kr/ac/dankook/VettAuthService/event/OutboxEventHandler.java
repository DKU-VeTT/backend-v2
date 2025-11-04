package kr.ac.dankook.VettAuthService.event;

import kr.ac.dankook.VettAuthService.entity.outbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxEventHandler {

    private final OutboxEventPublisher outboxEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePublishEvent(OutboxEvent event){
        try (var scope = event.getSnapshot().setThreadLocals()) {
            String id = event.getId();
            String eventTopic = event.getEventTopic();
            String payload = event.getPayload();
            String partitionKey = event.getPartitionKey();
            outboxEventPublisher.publishOutboxEvent(id,eventTopic,partitionKey,payload);
        }
    }
}
