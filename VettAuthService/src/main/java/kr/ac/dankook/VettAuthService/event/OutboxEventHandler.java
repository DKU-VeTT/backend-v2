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
        String id = event.getId();
        String eventType = event.getEventType();
        String payload = event.getPayload();
        String partitionKey = event.getPartitionKey();
        outboxEventPublisher.publishOutboxEvent(id,eventType,partitionKey,payload);
    }
}
