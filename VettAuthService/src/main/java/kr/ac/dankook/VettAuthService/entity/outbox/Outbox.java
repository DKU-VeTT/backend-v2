package kr.ac.dankook.VettAuthService.entity.outbox;

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
    // Domain : "USER"
    @Column(nullable = false)
    private String eventDomain;

    // Type : "user.event.deleted"
    @Column(nullable = false)
    private String eventType;

    // Payload : "{ key : "" ...  }"
    @Lob
    @Column(nullable = false)
    private String payload;

    private String partitionKey;

    @Column(nullable = false)
    private LocalDateTime timestamp;



    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Builder
    public Outbox(String id, String eventDomain, String eventType, String payload,String partitionKey,OutboxStatus status) {
        this.id = id;
        this.eventDomain = eventDomain;
        this.eventType = eventType;
        this.payload = payload;
        this.partitionKey = partitionKey;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

}
