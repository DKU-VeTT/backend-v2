package kr.ac.dankook.VettChatService.service;

import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatService.entity.*;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettChatService.event.SendChatEventPublisher;
import kr.ac.dankook.VettChatService.repository.ChatMessageRepository;
import kr.ac.dankook.VettChatService.repository.ChatRoomParticipantRepository;
import kr.ac.dankook.VettChatService.repository.ChatRoomRepository;
import kr.ac.dankook.VettChatService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomJoinService {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SendChatEventPublisher chatEventPublisher;

    public boolean isJoinChatRoom(Long roomId, String memberId){

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        Optional<ChatRoomParticipant> participant = chatRoomParticipantRepository
                .findByChatRoomAndMemberId(chatRoom ,memberId);
        return participant.isPresent();
    }

    @Transactional
    public ChatRoomResponse joinChatRoom(Long roomId, String nickname, String memberId){

        ChatRoom chatRoom = chatRoomRepository.findByIdWithOptimisticLock(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        if(isJoinChatRoom(roomId,memberId)){
            throw new CustomException(ErrorCode.ALREADY_JOIN_CHATROOM);
        }
        chatRoom.increaseParticipants();
        if(chatRoom.getOwnerId().equals(memberId)){
            chatRoom.updateChatRoomStatus(ChatRoomStatus.READ_WRITE);
        }
        chatRoomRepository.saveAndFlush(chatRoom);

        ChatRoomParticipant newParticipant = ChatRoomParticipant.builder()
                .chatRoom(chatRoom).memberId(memberId).nickname(nickname).build();
        chatRoomParticipantRepository.save(newParticipant);
        chatEventPublisher.sendChatMessage(makeMetaMessagePayload(chatRoom,memberId,nickname,MessageType.ENTER));
        return new ChatRoomResponse(newParticipant);
    }

    @Transactional
    public void leaveChatRoom(Long roomId, String memberId){

        String encryptRoomId = EncryptionUtil.encrypt(roomId);
        ChatRoom chatRoom = chatRoomRepository.findByIdWithOptimisticLock(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomAndMemberId(chatRoom, memberId).orElseThrow(() -> new EntityNotFoundException("참여하고 있는 사용자를 찾을 수 없습니다."));
        int currentCnt = chatRoom.decreaseParticipants();
        
        if (currentCnt <= 0){
            chatRoomParticipantRepository.delete(participant);
            chatRoomRepository.delete(chatRoom);
            List<ChatMessage> messages = chatMessageRepository.findByRoomId(encryptRoomId);
            chatMessageRepository.deleteAll(messages);
            return;
        }
        if(chatRoom.getOwnerId().equals(memberId)){
            chatRoom.updateChatRoomStatus(ChatRoomStatus.READ_ONLY);
        }
        chatRoomRepository.save(chatRoom);
        chatRoomParticipantRepository.delete(participant);
        chatEventPublisher.sendChatMessage(makeMetaMessagePayload(chatRoom, memberId, participant.getNickname(),MessageType.QUIT));
    }

    private ChatMessageRequest makeMetaMessagePayload(ChatRoom chatRoom,String memberId, String nickname, MessageType type){

        String messages = "";
        if (type == MessageType.ENTER){
            messages = String.format("%s님이 입장하였습니다.",nickname);
        }else if (type == MessageType.QUIT) messages = String.format("%s님이 퇴장하였습니다.",nickname);
        return ChatMessageRequest.builder()
                .roomId(EncryptionUtil.encrypt(chatRoom.getId()))
                .memberId(memberId)
                .message(messages)
                .nickname(nickname)
                .type(type).build();
    }
}
