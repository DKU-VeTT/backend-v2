package kr.ac.dankook.VettChatService.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id;
    @Field("room_id")
    private String roomId;
    @Field("member_id")
    private String memberId;
    private String nickname;
    private String content;
    private LocalDateTime time;
    private MessageType type;

    @Builder
    public ChatMessage(String roomId, String memberId, String content, String nickname, MessageType type) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.content = content;
        this.nickname = nickname;
        this.time = LocalDateTime.now();
        this.type = type;
    }
}
