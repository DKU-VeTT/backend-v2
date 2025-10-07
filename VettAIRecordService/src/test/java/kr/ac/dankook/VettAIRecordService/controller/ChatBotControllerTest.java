package kr.ac.dankook.VettAIRecordService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettAIRecordService.document.ChatBotRole;
import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotHistoryResponse;
import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotRoomResponse;
import kr.ac.dankook.VettAIRecordService.entity.Passport;
import kr.ac.dankook.VettAIRecordService.interceptor.AuthenticationInterceptor;
import kr.ac.dankook.VettAIRecordService.service.ChatBotHistoryService;
import kr.ac.dankook.VettAIRecordService.service.ChatBotRoomService;
import kr.ac.dankook.VettAIRecordService.util.PassportMemberArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ChatBotController.class)
@AutoConfigureDataJpa
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
class ChatBotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ChatBotRoomService chatBotRoomService;

    @MockitoBean
    ChatBotHistoryService chatBotHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    PassportMemberArgumentResolver passportMemberArgumentResolver;

    @MockitoBean
    AuthenticationInterceptor authenticationInterceptor;

    private final String title = "Title";
    private final String updateTitle = "UpdateTitle";
    private final String sessionId = "session";
    private final String content = "Content";

    @BeforeEach
    void setup(){
        given(authenticationInterceptor.preHandle(any(),any(),any())).willReturn(true);
        given(passportMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(passportMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(new Passport("user-1", "테스터", "test@test.com", "testuser", "USER"));
    }

    @Test
    @DisplayName("챗봇 생성 API")
    void createdChatBot() throws Exception {

        ChatBotRoomResponse resp = createMockResponse(title);

        when(chatBotRoomService.saveNewChatBotRoom("user-1", title)).thenReturn(resp);
        mockMvc.perform(post("/api/v1/chatbot/room")
                        .param("title", title)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.id").value("ENC-1"));
    }

    @Test
    @DisplayName("챗봇 수정 API")
    void updatedChatBot() throws Exception{

        when(chatBotRoomService.updateChatBotRoom(any(),any())).thenReturn(true);
        mockMvc.perform(patch("/api/v1/chatbot/room/" + sessionId + "/" + updateTitle)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("챗봇 채팅 가져오기 API")
    void getChatBotMessage() throws Exception{
        ChatBotHistoryResponse res1 = createMockChatResponse();
        when(chatBotHistoryService.getAllChatBotHistory(sessionId)).thenReturn(List.of(res1));
        mockMvc.perform(get("/api/v1/chatbot/" + sessionId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].content").value(content))
                .andExpect(jsonPath("$.data[0].sessionId").value(sessionId))
                .andExpect(jsonPath("$.data[0].role").value(ChatBotRole.human.toString()));
    }

    @Test
    @DisplayName("챗봇 삭제 API")
    void deleteChatBot() throws Exception{
        mockMvc.perform(get("/api/v1/chatbot/room" + sessionId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("나의 챗봇 가져오기 API")
    void getMyChatbot() throws  Exception{
        ChatBotRoomResponse resp1 = createMockResponse(title);
        ChatBotRoomResponse resp2 = createMockResponse(updateTitle);

        when(chatBotRoomService.getAllChatBotRoomByMember("user-1"))
                .thenReturn(List.of(resp1,resp2));
        mockMvc.perform(get("/api/v1/chatbot/rooms")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].title").value(title));
    }

    private ChatBotRoomResponse createMockResponse(String title){
        ChatBotRoomResponse resp = new ChatBotRoomResponse();
        resp.setId("ENC-1");
        resp.setTitle(title);
        resp.setTime(LocalDateTime.now());
        return resp;
    }

    private ChatBotHistoryResponse createMockChatResponse(){
        ChatBotHistoryResponse res1 = new ChatBotHistoryResponse();
        res1.setSessionId(sessionId);
        res1.setTime(LocalDateTime.now());
        res1.setRole(ChatBotRole.human);
        res1.setContent(content);
        return res1;
    }
}
