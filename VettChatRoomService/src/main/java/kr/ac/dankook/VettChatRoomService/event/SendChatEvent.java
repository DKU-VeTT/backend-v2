package kr.ac.dankook.VettChatRoomService.event;

import kr.ac.dankook.VettChatRoomService.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SendChatEvent {
    private String roomId;
    private String nickname;
    private MessageType messageType;
}
