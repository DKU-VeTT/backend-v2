package kr.ac.dankook.VettChatRoomService.entity;

import jakarta.persistence.*;
import kr.ac.dankook.VettChatRoomService.error.ErrorCode;
import kr.ac.dankook.VettChatRoomService.error.exception.CustomException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<ChatRoomParticipant> chatRoomParticipants = new ArrayList<>();

    @Builder
    public ChatRoom(String name, String description, int maxParticipants,String ownerId) {
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.ownerId = ownerId;
        this.currentParticipants = 1;
        this.status = ChatRoomStatus.READ_WRITE;
        this.lastMessageNumber = 0L;
    }

    public void increaseParticipants() {
        if (this.currentParticipants >= this.maxParticipants){
            throw new CustomException(ErrorCode.EXCEED_PARTICIPANT);
        }
        this.currentParticipants++;
    }

    public int decreaseParticipants() {
        if (this.currentParticipants <= 0){
            throw new CustomException(ErrorCode.RANGE_ERROR_PARTICIPANT);
        }
        return --this.currentParticipants;
    }


    public void updateChatRoomStatus(ChatRoomStatus status){
        this.status = status;
    }
}
