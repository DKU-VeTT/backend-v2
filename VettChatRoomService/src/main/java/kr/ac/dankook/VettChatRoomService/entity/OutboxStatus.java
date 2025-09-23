package kr.ac.dankook.VettChatRoomService.entity;

public enum OutboxStatus {
    READY_TO_PUBLISH,
    PUBLISHED,
    FAILED,
    PERMANENT_FAILURE,
}
