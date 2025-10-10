package kr.ac.dankook.VettAuthService.entity.outbox;

public enum OutboxStatus {
    READY_TO_PUBLISH,
    PUBLISHED,
    FAILED,
    PERMANENT_FAILURE,
}
