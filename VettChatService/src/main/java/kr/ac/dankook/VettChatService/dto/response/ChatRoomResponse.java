package kr.ac.dankook.VettChatService.dto.response;

import kr.ac.dankook.VettChatService.entity.ChatRoom;
import kr.ac.dankook.VettChatService.entity.ChatRoomStatus;
import kr.ac.dankook.VettChatService.util.EncryptionUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomResponse {

    private String roomId;
    private String name;
    private String description;
    private int currentParticipants;
    private int maxParticipants;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private LocalDateTime createdAt;
    private Long currentReadNumber;
    private Long lastMessageNumber;
    private String nickname;
    private ChatRoomStatus status;

    @Builder
    public ChatRoomResponse(ChatRoom chatRoom) {
        this.roomId = EncryptionUtil.encrypt(chatRoom.getId());
        this.name = chatRoom.getName();
        this.description = chatRoom.getDescription();
        this.currentParticipants = chatRoom.getCurrentParticipants();
        this.lastMessage = chatRoom.getLastMessage();
        this.createdAt = chatRoom.getCreatedDateTime();
        this.lastMessageTime = chatRoom.getLastMessageTime();
        this.maxParticipants = chatRoom.getMaxParticipants();
        this.status = chatRoom.getStatus();
    }
}
