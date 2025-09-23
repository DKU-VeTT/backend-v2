package kr.ac.dankook.VettChatService.service;

import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.dto.response.ChatResponse;
import kr.ac.dankook.VettChatService.entity.ChatMessage;
import kr.ac.dankook.VettChatService.event.SendChatEventPublisher;
import kr.ac.dankook.VettChatService.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SendChatEventPublisher chatEventPublisher;
    private final ChatMessageRepository chatMessageRepository;

    public void sendChatMessage(ChatMessageRequest request){
        chatEventPublisher.sendChatMessage(request);
    }

    public void saveChatMessage(ChatMessageRequest request){
        ChatMessage chatMessage = ChatMessage.builder()
                .type(request.getType())
                .memberId(request.getMemberId())
                .roomId(request.getRoomId())
                .nickname(request.getNickname())
                .content(request.getMessage()).build();
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatResponse> getAllChats(String roomId){
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(roomId);
        return chatMessages.stream().map(ChatResponse::new).toList();
    }
}
