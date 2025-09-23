package kr.ac.dankook.VettChatRoomService.repository;

import kr.ac.dankook.VettChatRoomService.entity.ChatRoom;
import kr.ac.dankook.VettChatRoomService.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant,Long> {

    Optional<ChatRoomParticipant> findByChatRoomAndMemberId(ChatRoom chatRoom, String memberId);

    List<ChatRoomParticipant> findByChatRoom(ChatRoom chatRoom);

    @Query("select p from ChatRoomParticipant p JOIN FETCH p.chatRoom where p.memberId = :memberId")
    List<ChatRoomParticipant> findByMemberWithFetchJoin(@Param("memberId") String memberId);
}
