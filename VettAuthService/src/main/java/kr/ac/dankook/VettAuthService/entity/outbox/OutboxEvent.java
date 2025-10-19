package kr.ac.dankook.VettAuthService.entity.outbox;

import io.micrometer.context.ContextSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {

    private String id;
    private String eventDomain;
    private String eventType;
    private String payload;
    private String partitionKey;
    private ContextSnapshot snapshot;

    public OutboxEvent(Outbox outbox) {
        this.id = outbox.getId();
        this.eventDomain = outbox.getEventDomain();
        this.eventType = outbox.getEventType();
        this.payload = outbox.getPayload();
        this.partitionKey = outbox.getPartitionKey();
    }

    public OutboxEvent(Outbox outbox,ContextSnapshot snapshot) {
        this.id = outbox.getId();
        this.eventDomain = outbox.getEventDomain();
        this.eventType = outbox.getEventType();
        this.payload = outbox.getPayload();
        this.partitionKey = outbox.getPartitionKey();
        this.snapshot = snapshot;
    }
}
