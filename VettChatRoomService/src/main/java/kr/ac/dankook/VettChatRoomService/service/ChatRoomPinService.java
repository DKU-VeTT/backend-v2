package kr.ac.dankook.VettChatRoomService.service;

import kr.ac.dankook.VettChatRoomService.entity.ChatRoom;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoomParticipant;
import kr.ac.dankook.VettChatRoomService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettChatRoomService.repository.ChatRoomParticipantRepository;
import kr.ac.dankook.VettChatRoomService.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomPinService {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public boolean toggleChatRoomPin(Long roomId,String memberId){

        ChatRoom chatRoom = chatRoomRepository.findByIdWithOptimisticLock(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomAndMemberId(chatRoom, memberId).orElseThrow(() -> new EntityNotFoundException("현재 참여하고 있는 채팅방이 아닙니다."));
        participant.togglePin();
        chatRoomParticipantRepository.save(participant);
        return participant.isPin();
    }
}
