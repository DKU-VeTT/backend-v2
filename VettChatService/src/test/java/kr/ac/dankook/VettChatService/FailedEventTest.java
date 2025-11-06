package kr.ac.dankook.VettChatService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.entity.MessageType;
import kr.ac.dankook.VettChatService.event.FailedEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static kr.ac.dankook.VettChatService.event.ChatEventSubscriber.CHAT_TOPIC;

@SpringBootTest
@ActiveProfiles("local")
public class FailedEventTest {

    @Autowired
    private FailedEventPublisher failedEventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("발행 실패 이벤트 테스트")
    public void pdltRecordTest() throws JsonProcessingException {
        ChatMessageRequest req = ChatMessageRequest.builder()
                        .type(MessageType.ENTER)
                        .roomId("TEST_ROOM")
                        .memberId("TEST_MEMBER")
                        .nickname("TEST_NICKNAME")
                        .message("MESSAGE").build();
        String payload = objectMapper.writeValueAsString(req);
        failedEventPublisher.sendFailedEvent(CHAT_TOPIC, "ChatEventPublisher", "TEST_ROOM", payload,"ERROR");
    }
}
