package kr.ac.dankook.VettChatRoomService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="chat_room_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Setter
    private Long currentReadNumber;
    @Setter
    private String nickname;

    @Builder
    public ChatRoomParticipant(String memberId, ChatRoom chatRoom, String nickname){
        this.memberId = memberId;
        this.chatRoom = chatRoom;
        this.nickname = nickname;
        this.currentReadNumber = 0L;
    }
}
