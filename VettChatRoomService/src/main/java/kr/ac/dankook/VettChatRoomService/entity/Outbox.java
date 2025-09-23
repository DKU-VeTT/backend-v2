package kr.ac.dankook.VettChatRoomService.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Outbox{

    @Id
    private String id;
    @Column(nullable = false)
    private String domain;

    // Type : "UserModified"
    @Column(nullable = false)
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Builder
    public Outbox(String id, String domain, String eventType, String payload, LocalDateTime timestamp, OutboxStatus status) {
        this.id = id;
        this.domain = domain;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

}
