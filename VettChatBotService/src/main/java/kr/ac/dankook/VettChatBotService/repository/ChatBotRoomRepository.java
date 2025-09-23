package kr.ac.dankook.VettChatBotService.repository;

import kr.ac.dankook.VettChatBotService.entity.ChatBotRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatBotRoomRepository extends JpaRepository<ChatBotRoom,Long> {
    List<ChatBotRoom> findChatBotRoomByOwnerId(String ownerId);
}
