package kr.ac.dankook.VettAuthService.entity.outbox;

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

    public OutboxEvent(Outbox outbox) {
        this.id = outbox.getId();
        this.eventDomain = outbox.getEventDomain();
        this.eventType = outbox.getEventType();
        this.payload = outbox.getPayload();
        this.partitionKey = outbox.getPartitionKey();
    }
}
