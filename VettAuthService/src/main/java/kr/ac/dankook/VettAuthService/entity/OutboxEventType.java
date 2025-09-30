package kr.ac.dankook.VettAuthService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OutboxEventType {

    USER_CREATED("USER","user.event.created"),
    USER_DELETED("USER","user.event.deleted");

    private final String domain;
    private final String eventType;
}
