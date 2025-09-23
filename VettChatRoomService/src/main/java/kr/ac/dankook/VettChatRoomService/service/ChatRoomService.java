package kr.ac.dankook.VettChatRoomService.service;

import kr.ac.dankook.VettChatRoomService.dto.request.ChatRoomCreateRequest;
import kr.ac.dankook.VettChatRoomService.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoom;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoomParticipant;
import kr.ac.dankook.VettChatRoomService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettChatRoomService.repository.ChatRoomParticipantRepository;
import kr.ac.dankook.VettChatRoomService.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;


    public List<ChatRoomResponse> getMyManagerChatRoomList(String ownerId){
        List<ChatRoom> chatRooms = chatRoomRepository
                .findByManagerMember(ownerId);
        return chatRooms.stream().map(ChatRoomResponse::new).toList();
    }

    public List<ChatRoomResponse> getMyChatRoomList(String memberId) {
        List<ChatRoomParticipant> chatRooms = chatRoomParticipantRepository
                .findByMemberWithFetchJoin(memberId);
        return chatRooms.stream().map(ChatRoomResponse::new).toList();
    }

    @Transactional
    public ChatRoomResponse saveNewChatRoom(String ownerId, ChatRoomCreateRequest chatRoomCreateRequest) {

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomCreateRequest.getName())
                .description(chatRoomCreateRequest.getDescription())
                .maxParticipants(chatRoomCreateRequest.getMaxParticipants())
                .ownerId(ownerId).build();
        ChatRoom newEntity = chatRoomRepository.save(chatRoom);

        ChatRoomParticipant newParticipant = ChatRoomParticipant.builder()
                .chatRoom(chatRoom).memberId(ownerId).nickname(chatRoomCreateRequest.getNickname()).build();
        chatRoomParticipantRepository.save(newParticipant);
        return new ChatRoomResponse(newEntity);
    }

    public List<ChatRoomResponse> getAllChatRoomList(){
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream().map(ChatRoomResponse::new).toList();
    }

    public List<ChatRoomResponse> getChatRoomListByKeyword(String keyword){
        List<ChatRoom> chatRooms = chatRoomRepository.searchByKeyword(keyword);
        return chatRooms.stream().map(ChatRoomResponse::new).toList();
    }

    @Transactional
    public void updateUnreadMessages(Long roomId, String memberId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomAndMemberId(chatRoom, memberId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방 참가자를 찾을 수 없습니다."));
        participant.setCurrentReadNumber(chatRoom.getLastMessageNumber());
        chatRoomParticipantRepository.save(participant);
    }

}
