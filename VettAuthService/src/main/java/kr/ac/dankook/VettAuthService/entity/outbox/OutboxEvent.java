package kr.ac.dankook.VettAuthService.entity.outbox;

import io.micrometer.context.ContextSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {

    private String id;
    private String eventDomain;
    private String eventTopic;
    private String payload;
    private String partitionKey;
    private ContextSnapshot snapshot;

    public OutboxEvent(Outbox outbox) {
        this.id = outbox.getId();
        this.eventDomain = outbox.getEventDomain();
        this.eventTopic = outbox.getEventTopic();
        this.payload = outbox.getPayload();
        this.partitionKey = outbox.getPartitionKey();
    }

    public OutboxEvent(Outbox outbox,ContextSnapshot snapshot) {
        this.id = outbox.getId();
        this.eventDomain = outbox.getEventDomain();
        this.eventTopic = outbox.getEventTopic();
        this.payload = outbox.getPayload();
        this.partitionKey = outbox.getPartitionKey();
        this.snapshot = snapshot;
    }
}
