package kr.ac.dankook.VettChatBotService.service;

import kr.ac.dankook.VettChatBotService.document.ChatBotHistory;
import kr.ac.dankook.VettChatBotService.dto.response.ChatBotHistoryResponse;
import kr.ac.dankook.VettChatBotService.repository.ChatBotHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotHistoryService {

    private final ChatBotHistoryRepository chatBotHistoryRepository;

    public List<ChatBotHistoryResponse> getAllChatBotHistory(String sessionId){
        List<ChatBotHistory> lists = chatBotHistoryRepository.findBySessionId(sessionId);
        return lists.stream().map(ChatBotHistoryResponse::new).toList();
    }

    public void deleteChatBotHistory(String sessionId){
        chatBotHistoryRepository.deleteBySessionId(sessionId);
    }
}
