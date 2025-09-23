package kr.ac.dankook.VettChatRoomService.dto.request;

import kr.ac.dankook.VettChatRoomService.entity.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageRequest {

    private MessageType type;
    private String memberId;
    private String nickname;
    private String roomId;
    private String message;
    private LocalDateTime time;

    @Builder
    public ChatMessageRequest(
            MessageType type, String memberId, String roomId, String message,String nickname) {
        this.type = type;
        this.memberId = memberId;
        this.roomId = roomId;
        this.nickname = nickname;
        this.message = message;
        this.time = LocalDateTime.now();
    }

}
