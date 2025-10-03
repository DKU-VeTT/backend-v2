package kr.ac.dankook.VettChatBotService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatBotService.service.ChatBotHistoryService;
import kr.ac.dankook.VettChatBotService.service.ChatBotRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChatBotController.class)
@AutoConfigureDataJpa
public class ChatBotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatBotRoomService chatBotRoomService;

    @MockitoBean
    private ChatBotHistoryService chatBotHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("챗봇 생성 API")
    void createdChatBot() throws Exception {
        String title = "Demo chat bot title";

    }

}
