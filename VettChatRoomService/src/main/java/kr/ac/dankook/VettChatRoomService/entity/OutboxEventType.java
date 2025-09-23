package kr.ac.dankook.VettChatRoomService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OutboxEventType {

    CHAT_ROOM_DELETED("CHAT","chat.room.event.deleted"),
    CHAT_ROOM_MESSAGE("CHAT","chat.room.event.message");

    private final String domain;
    private final String eventType;
}
