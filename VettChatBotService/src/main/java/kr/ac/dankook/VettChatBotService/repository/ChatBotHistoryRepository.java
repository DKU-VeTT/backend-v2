package kr.ac.dankook.VettChatBotService.repository;


import kr.ac.dankook.VettChatBotService.document.ChatBotHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatBotHistoryRepository extends MongoRepository<ChatBotHistory,String> {
    List<ChatBotHistory> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
