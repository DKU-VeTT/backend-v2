package kr.ac.dankook.VettChatService.repository;

import kr.ac.dankook.VettChatService.entity.ChatRoom;
import kr.ac.dankook.VettChatService.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant,Long> {

    Optional<ChatRoomParticipant> findByChatRoomAndMemberId(ChatRoom chatRoom, String memberId);

    @Query("select p from ChatRoomParticipant p JOIN FETCH p.chatRoom where p.memberId = :memberId")
    List<ChatRoomParticipant> findByMemberWithFetchJoin(@Param("memberId") String memberId);
}
