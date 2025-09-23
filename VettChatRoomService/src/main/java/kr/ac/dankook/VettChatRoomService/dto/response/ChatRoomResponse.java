package kr.ac.dankook.VettChatRoomService.dto.response;

import kr.ac.dankook.VettChatRoomService.entity.ChatRoom;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoomParticipant;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoomStatus;
import kr.ac.dankook.VettChatRoomService.util.EncryptionUtil;
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

    @Builder
    public ChatRoomResponse(ChatRoomParticipant participant){
        ChatRoom chatRoom = participant.getChatRoom();
        this.roomId = EncryptionUtil.encrypt(chatRoom.getId());
        this.name = chatRoom.getName();
        this.description = chatRoom.getDescription();
        this.currentParticipants = chatRoom.getCurrentParticipants();
        this.lastMessage = chatRoom.getLastMessage();
        this.createdAt = chatRoom.getCreatedDateTime();
        this.lastMessageTime = chatRoom.getLastMessageTime();
        this.currentReadNumber = participant.getCurrentReadNumber();
        this.lastMessageNumber = chatRoom.getLastMessageNumber();
        this.nickname = participant.getNickname();
        this.maxParticipants = chatRoom.getMaxParticipants();
        this.status = chatRoom.getStatus();
    }
}
