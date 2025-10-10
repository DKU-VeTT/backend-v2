package kr.ac.dankook.VettAuthService.entity.outbox;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OutboxEventType {

    USER_DELETED("USER","user.event.deleted");

    private final String eventDomain;
    private final String eventType;
}
