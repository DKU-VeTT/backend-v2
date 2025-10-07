package kr.ac.dankook.VettAIRecordService.service;


import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotRoomResponse;
import kr.ac.dankook.VettAIRecordService.entity.ChatBotRoom;
import kr.ac.dankook.VettAIRecordService.repository.ChatBotRoomRepository;
import kr.ac.dankook.VettAIRecordService.util.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("local")
@Slf4j
public class ChatBotRoomServiceTest {

    @Autowired
    private ChatBotRoomService chatBotRoomService;

    @Autowired
    private ChatBotRoomRepository chatBotRoomRepository;

    private final String ownerId = "Owner";
    private final String title = "Test title";
    private final String title2 = "Test title2";
    private final String updatedTitle = "Updated Title";

    @Test
    @DisplayName("챗봇 생성 테스트 - 실제 DB 사용")
    @Transactional
    void createChatBotTest() {
        ChatBotRoomResponse res = chatBotRoomService.saveNewChatBotRoom(ownerId, title);
        assertThat(res.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("챗봇 제목 변경 테스트 - 실제 DB 사용")
    @Transactional
    void updateChatBotTitleTest(){

        ChatBotRoomResponse res = chatBotRoomService.saveNewChatBotRoom(ownerId,title);
        boolean isSuccess = chatBotRoomService.updateChatBotRoom(res.getId(),updatedTitle);
        ChatBotRoom chatBotRoom = chatBotRoomRepository.findById(EncryptionUtil.decrypt(res.getId()))
                        .orElse(null);

        assertThat(chatBotRoom).isNotNull();
        assertThat(isSuccess).isTrue();
        assertThat(chatBotRoom.getTitle()).isEqualTo(updatedTitle);

    }

    @Test
    @DisplayName("나의 챗봇 가져오기 테스트 - 실제 DB 사용")
    @Transactional
    void getMyChatBotTest(){

        chatBotRoomService.saveNewChatBotRoom(ownerId,title);
        chatBotRoomService.saveNewChatBotRoom(ownerId,title2);

        List<ChatBotRoomResponse> rooms = chatBotRoomService.getAllChatBotRoomByMember(ownerId);

        assertThat(rooms.size()).isEqualTo(2);
        assertThat(rooms.get(0).getTitle()).isEqualTo(title);
        assertThat(rooms.get(1).getTitle()).isEqualTo(title2);
    }

    @Test
    @DisplayName("나의 챗봇 삭제 - 실제 DB 사용")
    @Transactional
    void deleteChatBotTest(){
        ChatBotRoomResponse res = chatBotRoomService.saveNewChatBotRoom(ownerId,title);

        chatBotRoomService.deleteChatBotRoom(res.getId());
        Optional<ChatBotRoom> room = chatBotRoomRepository.findById(EncryptionUtil.decrypt(res.getId()));
        assertThat(room.isEmpty()).isTrue();
    }
}
