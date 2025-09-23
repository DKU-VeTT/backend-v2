package kr.ac.dankook.VettChatService.repository;

import kr.ac.dankook.VettChatService.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage,String> {
    List<ChatMessage> findByRoomId(String roomId);
}
