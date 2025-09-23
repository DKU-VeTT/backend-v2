package kr.ac.dankook.VettChatRoomService.repository;

import jakarta.persistence.LockModeType;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select c from ChatRoom c where c.id = :id")
    Optional<ChatRoom> findByIdWithOptimisticLock(Long id);

    @Query("select c from ChatRoom c where c.ownerId = :ownerId")
    List<ChatRoom> findByManagerMember(@Param("ownerId") String ownerId);

    @Query("select c from ChatRoom c where c.name LIKE %:keyword% or c.description LIKE %:keyword%")
    List<ChatRoom> searchByKeyword(@Param("keyword") String keyword);
}
