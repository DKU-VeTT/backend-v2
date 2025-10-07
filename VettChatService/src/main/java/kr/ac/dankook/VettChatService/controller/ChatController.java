package kr.ac.dankook.VettChatService.controller;

import kr.ac.dankook.VettChatService.dto.request.ChatMessageRequest;
import kr.ac.dankook.VettChatService.dto.response.ApiResponse;
import kr.ac.dankook.VettChatService.dto.response.ChatResponse;
import kr.ac.dankook.VettChatService.service.ChatMetaService;
import kr.ac.dankook.VettChatService.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final ChatMetaService chatMetaService;

    // Prefix /pub
    @MessageMapping("/chat/message")
    public void sendMessage(
            @Payload ChatMessageRequest request) {
        chatMetaService.updateRoomMetadataAsync(request);
        chatService.sendChatMessage(request);
        chatService.saveChatMessage(request);
    }

    @GetMapping("/api/v1/chat/messages/{roomId}")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getAllChatMessages(
            @PathVariable String roomId
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatService.getAllChats(roomId)));
    }
}
