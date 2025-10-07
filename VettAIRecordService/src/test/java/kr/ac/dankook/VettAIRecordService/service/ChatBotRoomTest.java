package kr.ac.dankook.VettAIRecordService.service;

import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotRoomResponse;
import kr.ac.dankook.VettAIRecordService.entity.ChatBotRoom;
import kr.ac.dankook.VettAIRecordService.repository.ChatBotRoomRepository;
import kr.ac.dankook.VettAIRecordService.util.EncryptionUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatBotRoomTest {

    @Mock
    ChatBotRoomRepository chatBotRoomRepository;

    @InjectMocks
    ChatBotRoomService chatBotRoomService;

    @Captor
    ArgumentCaptor<ChatBotRoom> chatBotRoomCaptor;

    @Test
    @DisplayName("채팅 봇 생성 테스트")
    void createChatBotProcess() {
        when(chatBotRoomRepository.save(any(ChatBotRoom.class)))
                .thenAnswer(inv -> {
                    ChatBotRoom e = inv.getArgument(0);
                    ReflectionTestUtils.setField(e, "id", 1L);
                    ReflectionTestUtils.setField(e, "createdDateTime",
                            LocalDateTime.of(2025, 1, 1, 0, 0));
                    return e;
                });
        ChatBotRoomResponse res =
                chatBotRoomService.saveNewChatBotRoom("Test1234", "Demo Title");

        assertThat(res.getId()).isNotBlank();
        assertThat(res.getTitle()).isEqualTo("Demo Title");
        assertThat(res.getTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 0, 0));

        verify(chatBotRoomRepository).save(chatBotRoomCaptor.capture());
        ChatBotRoom saved = chatBotRoomCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Demo Title");
        assertThat(saved.getOwnerId()).isEqualTo("Test1234");
    }

    @Test
    @DisplayName("나의 채팅 봇 가져오기")
    void getMyChatBotProcess(){
            ChatBotRoom e1 = ChatBotRoom.builder()
                    .title("Test1").ownerId("testuser").build();
            ChatBotRoom e2 = ChatBotRoom.builder()
                    .title("Test2").ownerId("testuser").build();

            ReflectionTestUtils.setField(e1,"id",1L);
            ReflectionTestUtils.setField(e2,"id",2L);

            when(chatBotRoomRepository.findChatBotRoomByOwnerId("testuser"))
                    .thenReturn(List.of(e1,e2));

            List<ChatBotRoomResponse> res =
                    chatBotRoomService.getAllChatBotRoomByMember("testuser");

            assertThat(res).hasSize(2);
            assertThat(res.get(0).getTitle()).isEqualTo("Test1");
            assertThat(res.get(1).getTitle()).isEqualTo("Test2");
    }

    @Test
    @DisplayName("채팅방 제목 업데이트")
    void updateChatBotProcess(){
        try(MockedStatic<EncryptionUtil> mocked = mockStatic(EncryptionUtil.class)){

            mocked.when(() -> EncryptionUtil.decrypt("session")).thenReturn(1L);

            String updateTitle = "UpdateTitle";
            ChatBotRoom entity = ChatBotRoom.builder()
                    .title("Original Title")
                    .ownerId("owner")
                    .build();
            ReflectionTestUtils.setField(entity, "id", 1L);

            when(chatBotRoomRepository.findById(1L))
                    .thenReturn(Optional.of(entity));

            boolean isSuccess = chatBotRoomService.updateChatBotRoom("session",updateTitle);

            verify(chatBotRoomRepository).save(chatBotRoomCaptor.capture());
            ChatBotRoom botRoom = chatBotRoomCaptor.getValue();

            assertThat(botRoom.getTitle()).isEqualTo(updateTitle);
            assertThat(botRoom.getId()).isEqualTo(1L);
            assertThat(isSuccess).isEqualTo(true);
        }
    }
}
