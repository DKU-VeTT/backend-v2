package kr.ac.dankook.VettAIRecordService.repository;


import kr.ac.dankook.VettAIRecordService.document.ChatBotHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatBotHistoryRepository extends MongoRepository<ChatBotHistory,String> {
    List<ChatBotHistory> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
