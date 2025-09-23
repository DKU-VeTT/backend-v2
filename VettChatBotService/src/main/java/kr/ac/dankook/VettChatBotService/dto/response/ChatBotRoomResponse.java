package kr.ac.dankook.VettChatBotService.dto.response;

import kr.ac.dankook.VettChatBotService.entity.ChatBotRoom;
import kr.ac.dankook.VettChatBotService.util.EncryptionUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatBotRoomResponse {

    private String id;
    private String title;
    private LocalDateTime time;

    @Builder
    public ChatBotRoomResponse(ChatBotRoom chatBotRoom){
        this.id = EncryptionUtil.encrypt(chatBotRoom.getId());
        this.title = chatBotRoom.getTitle();
        this.time = chatBotRoom.getCreatedDateTime();
    }
}
