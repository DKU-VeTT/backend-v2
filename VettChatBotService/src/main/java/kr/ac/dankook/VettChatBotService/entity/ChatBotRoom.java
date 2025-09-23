package kr.ac.dankook.VettChatBotService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_bot_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatBotRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerId;
    private String title;

    @Builder
    public ChatBotRoom(String ownerId, String title) {
        this.ownerId = ownerId;
        this.title = title;
    }

    public void updateTitle(String title){
        this.title = title;
    }
}