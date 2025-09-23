package kr.ac.dankook.VettChatService.repository;

import jakarta.persistence.LockModeType;
import kr.ac.dankook.VettChatService.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ChatRoom c where c.id = :id")
    Optional<ChatRoom> findByIdWithPessimisticLock(Long id);
}
