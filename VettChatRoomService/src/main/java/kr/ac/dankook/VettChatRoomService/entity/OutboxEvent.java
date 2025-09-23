package kr.ac.dankook.VettChatRoomService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {

    private String id;
    private String domain;
    private String eventType;
    private String payload;

    public OutboxEvent(Outbox outbox){
        this.id = outbox.getId();
        this.domain = outbox.getDomain();
        this.eventType = outbox.getEventType();
        this.payload = outbox.getPayload();
    }
}
