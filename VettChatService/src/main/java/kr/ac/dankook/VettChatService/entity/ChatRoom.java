package kr.ac.dankook.VettChatService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name="chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int maxParticipants = 300;
    private int currentParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long lastMessageNumber;

    @Version
    private Long version;

    @Column(nullable = false)
    private String ownerId;

    public void updateMessages(String lastMessage){
        this.lastMessage = lastMessage;
        this.lastMessageTime = LocalDateTime.now();
        this.lastMessageNumber++;
    }
}
