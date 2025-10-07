package kr.ac.dankook.VettAIRecordService.dto.response;

import kr.ac.dankook.VettAIRecordService.entity.ChatBotRoom;
import kr.ac.dankook.VettAIRecordService.util.EncryptionUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
