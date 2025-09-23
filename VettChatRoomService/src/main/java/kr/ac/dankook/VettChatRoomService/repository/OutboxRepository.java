package kr.ac.dankook.VettChatRoomService.repository;

import kr.ac.dankook.VettChatRoomService.entity.Outbox;
import kr.ac.dankook.VettChatRoomService.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox,String> {
    List<Outbox> findByStatus(OutboxStatus status);
}
