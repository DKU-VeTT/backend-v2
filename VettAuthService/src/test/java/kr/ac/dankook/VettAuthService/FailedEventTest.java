package kr.ac.dankook.VettAuthService;

import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxEventType;
import kr.ac.dankook.VettAuthService.event.FailedEventPublisher;
import kr.ac.dankook.VettAuthService.event.OutboxEventPublisher;
import kr.ac.dankook.VettAuthService.service.OutboxService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class FailedEventTest {

    @Autowired
    private FailedEventPublisher failedEventPublisher;

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OutboxEventPublisher outboxEventPublisher;

    @Test
    @DisplayName("발행 실패 이벤트 테스트")
    public void pdltRecordTest() {
        Outbox outbox = outboxService.makeMemberOutbox(OutboxEventType.USER_DELETED, "USER_KEY");
        String eventTopic = outbox.getEventTopic();
        String payload = outbox.getPayload();
        String partitionKey = outbox.getPartitionKey();
        failedEventPublisher.sendFailedEvent(eventTopic, "FailedEventTest", partitionKey, payload,"DETAIL_ERROR");
    }

    @Test
    @DisplayName("구독 실패 이벤트 테스트")
    public void dltRecordTest() {
        Outbox outbox = outboxService.makeMemberOutbox(OutboxEventType.USER_DELETED, "USER_KEY");
        String id = outbox.getId();
        String eventTopic = outbox.getEventTopic();
        String payload = outbox.getPayload();
        String partitionKey = outbox.getPartitionKey();
        outboxEventPublisher.publishOutboxEvent(id, eventTopic, partitionKey, payload);
    }
}
