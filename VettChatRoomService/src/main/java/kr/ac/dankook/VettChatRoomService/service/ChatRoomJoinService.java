package kr.ac.dankook.VettChatRoomService.service;

import kr.ac.dankook.VettChatRoomService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatRoomService.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatRoomService.entity.*;
import kr.ac.dankook.VettChatRoomService.error.ErrorCode;
import kr.ac.dankook.VettChatRoomService.error.exception.CustomException;
import kr.ac.dankook.VettChatRoomService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettChatRoomService.repository.ChatRoomParticipantRepository;
import kr.ac.dankook.VettChatRoomService.repository.ChatRoomRepository;
import kr.ac.dankook.VettChatRoomService.repository.OutboxRepository;
import kr.ac.dankook.VettChatRoomService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomJoinService {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatRoomOutboxService chatRoomOutboxService;
    private final OutboxRepository outboxRepository;

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

        // 입장 채팅 전송 -> 비관심사이므로 Outbox pattern 적용 X
        eventPublisher.publishEvent(makeMetaMessagePayload(chatRoom,memberId,nickname,MessageType.ENTER));
        return new ChatRoomResponse(newParticipant);
    }

    @Transactional
    public void leaveChatRoom(Long roomId,String memberId){

        String encryptRoomId = EncryptionUtil.encrypt(roomId);
        ChatRoom chatRoom = chatRoomRepository.findByIdWithOptimisticLock(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomAndMemberId(chatRoom, memberId).orElseThrow(() -> new EntityNotFoundException("참여하고 있는 사용자를 찾을 수 없습니다."));
        int currentCnt = chatRoom.decreaseParticipants();
        
        if (currentCnt <= 0){
            chatRoomParticipantRepository.delete(participant);
            chatRoomRepository.delete(chatRoom);
            // 채팅방에 속해있는 삭제 이벤트 발행 ( 트랜잭션 아웃박스 패턴 )
            Outbox outbox = chatRoomOutboxService.makeOutbox(encryptRoomId,OutboxEventType.CHAT_ROOM_DELETED);
            outboxRepository.save(outbox);
            eventPublisher.publishEvent(new OutboxEvent(outbox));
            return;
        }
        if(chatRoom.getOwnerId().equals(memberId)){
            chatRoom.updateChatRoomStatus(ChatRoomStatus.READ_ONLY);
        }
        chatRoomRepository.save(chatRoom);
        chatRoomParticipantRepository.delete(participant);
        // 퇴장 채팅 전송 -> 비관심사이므로 Outbox pattern 적용 x
        eventPublisher.publishEvent(makeMetaMessagePayload(chatRoom, memberId, participant.getNickname(),MessageType.QUIT));
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
