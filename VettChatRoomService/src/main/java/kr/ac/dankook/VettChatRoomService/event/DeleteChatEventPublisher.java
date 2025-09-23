package kr.ac.dankook.VettChatRoomService.event;

import kr.ac.dankook.VettChatRoomService.entity.OutboxEvent;
import kr.ac.dankook.VettChatRoomService.entity.OutboxStatus;
import kr.ac.dankook.VettChatRoomService.service.ChatRoomOutboxCacheService;
import kr.ac.dankook.VettChatRoomService.service.ChatRoomOutboxService;
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

import static kr.ac.dankook.VettChatRoomService.service.ChatRoomOutboxService.OUTBOX_TTL;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteChatEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ChatRoomOutboxCacheService chatRoomOutboxCacheService;
    private final ChatRoomOutboxService chatRoomOutboxService;
    private final RetryTemplate retryTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteChatMessages(OutboxEvent outboxEvent){

        String id = outboxEvent.getId();
        String eventType = outboxEvent.getEventType();
        String payload = outboxEvent.getPayload();

        if (chatRoomOutboxCacheService.checkIsAlreadyPublish(id)) return;
        CompletableFuture<SendResult<String,String>> future =
                kafkaTemplate.send(eventType,payload);

        future.whenComplete((result,ex) -> {
            if (ex == null){
                retryTemplate.execute(ctx -> {
                    chatRoomOutboxCacheService.setOutboxId(id,OUTBOX_TTL);
                    chatRoomOutboxService.convertOutboxStatus(id, OutboxStatus.PUBLISHED);
                    return null;
                });
            }else{
                log.error("Failed to send Kafka. eventId : {} / eventType : {}",id, eventType);
                retryTemplate.execute(ctx -> {
                    chatRoomOutboxCacheService.deleteOutboxId(id);
                    chatRoomOutboxService.convertOutboxStatus(id, OutboxStatus.FAILED);
                    return null;
                });
            }
        });
    }
}
