package kr.ac.dankook.VettAuthService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Outbox{

    // UUID Random PK
    @Id
    private String id;
    // Domain : "User"
    @Column(nullable = false)
    private String domain;

    // Type : "UserModified"
    @Column(nullable = false)
    private String eventType;

    // Payload : "{ key(User PK) : "", eventId : "", userId : "", name : "", email : "" }"
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
