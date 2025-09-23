package kr.ac.dankook.VettChatBotService.document;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "chat_bot_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatBotHistory {

    @Id
    private String id;
    @Field("session_id")
    private String sessionId;
    private ChatBotRole role;
    private String content;
    private LocalDateTime time;

    @Builder
    public ChatBotHistory(String sessionId,ChatBotRole role, String content) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.time = LocalDateTime.now();
    }
}
