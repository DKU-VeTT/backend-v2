package kr.ac.dankook.VettChatBotService.service;

import kr.ac.dankook.VettChatBotService.dto.response.ChatBotRoomResponse;
import kr.ac.dankook.VettChatBotService.entity.ChatBotRoom;
import kr.ac.dankook.VettChatBotService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettChatBotService.repository.ChatBotRoomRepository;
import kr.ac.dankook.VettChatBotService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotRoomService {

    private final ChatBotRoomRepository chatBotRoomRepository;
    private final ChatBotHistoryService chatBotHistoryService;

    @Transactional
    public ChatBotRoomResponse saveNewChatBotRoom(String ownerId, String title){
        ChatBotRoom chatBotRoom = ChatBotRoom.builder()
                .title(title).ownerId(ownerId).build();
        ChatBotRoom newEntity = chatBotRoomRepository.save(chatBotRoom);
        return new ChatBotRoomResponse(newEntity);
    }

    @Transactional
    public List<ChatBotRoomResponse> getAllChatBotRoomByMember(String ownerId){
        List<ChatBotRoom> lists = chatBotRoomRepository.findChatBotRoomByOwnerId(ownerId);
        return lists.stream().map(ChatBotRoomResponse::new).toList();
    }

    @Transactional
    public void deleteChatBotRoom(String sessionId){
        Long decryptId = EncryptionUtil.decrypt(sessionId);
        chatBotRoomRepository.deleteById(decryptId);
        chatBotHistoryService.deleteChatBotHistory(sessionId);
    }

    @Transactional
    public boolean updateChatBotRoom(String sessionId,String title){
        Long decryptId = EncryptionUtil.decrypt(sessionId);
        ChatBotRoom chatBotRoom = chatBotRoomRepository.findById(decryptId)
                .orElseThrow(() -> new EntityNotFoundException("챗봇방 정보를 찾을 수 없습니다."));
        chatBotRoom.updateTitle(title);
        chatBotRoomRepository.save(chatBotRoom);
        return true;
    }
}
