package kr.ac.dankook.VettChatService.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventType {

    MEMBER_DELETED("MEMBER","chat.room.event.deleted"),
    CHAT_ROOM_MESSAGE("CHAT","chat.room.event.message");

    private final String domain;
    private final String eventType;
}