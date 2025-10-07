package kr.ac.dankook.VettAIRecordService.service;

import kr.ac.dankook.VettAIRecordService.document.ChatBotHistory;
import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotHistoryResponse;
import kr.ac.dankook.VettAIRecordService.repository.ChatBotHistoryRepository;
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
