package kr.ac.dankook.VettChatService.service;

import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.entity.ChatRoom;
import kr.ac.dankook.VettChatService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettChatService.repository.ChatRoomRepository;
import kr.ac.dankook.VettChatService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMetaService {

    private final ChatRoomRepository chatRoomRepository;

    @Async
    @Transactional
    public void updateRoomMetadataAsync(ChatMessageRequest request) {
        Long roomId = EncryptionUtil.decrypt(request.getRoomId());
        ChatRoom room = chatRoomRepository.findByIdWithPessimisticLock(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방이 없습니다"));
        room.updateMessages(request.getMessage());
        chatRoomRepository.save(room);
    }
}
