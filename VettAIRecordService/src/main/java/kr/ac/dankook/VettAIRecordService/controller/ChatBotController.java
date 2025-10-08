package kr.ac.dankook.VettAIRecordService.controller;

import kr.ac.dankook.VettAIRecordService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettAIRecordService.dto.response.ApiResponse;
import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotHistoryResponse;
import kr.ac.dankook.VettAIRecordService.dto.response.ChatBotRoomResponse;
import kr.ac.dankook.VettAIRecordService.entity.Passport;
import kr.ac.dankook.VettAIRecordService.service.ChatBotHistoryService;
import kr.ac.dankook.VettAIRecordService.service.ChatBotRoomService;
import kr.ac.dankook.VettAIRecordService.service.IdempotencyService;
import kr.ac.dankook.VettAIRecordService.util.HashUtil;
import kr.ac.dankook.VettAIRecordService.util.PassportMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/ai/record/chatbot")
public class ChatBotController {

    private final ChatBotHistoryService chatBotHistoryService;
    private final ChatBotRoomService chatBotRoomService;
    private final IdempotencyService idempotencyService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<List<ChatBotHistoryResponse>>> getAllChatHistory(
            @PathVariable String sessionId
    ) {
        return ResponseEntity.status(200).body(new ApiResponse<>(
                true,
                200,
                chatBotHistoryService.getAllChatBotHistory(sessionId)
        ));
    }


    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatBotRoomResponse>>> getAllChatBotRoomsByMember(
            @PassportMember Passport passport
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatBotRoomService.getAllChatBotRoomByMember(passport.getKey())));
    }


    @PostMapping("/room")
    public ResponseEntity<ApiResponse<ChatBotRoomResponse>> saveNewChatBotRoom(
            @RequestParam("title") String title,
            @RequestHeader("Idempotency-Key") String key,
            @PassportMember Passport passport
    ){
        String hash = HashUtil.sha256HexOfParts(key,title);
        ChatBotRoomResponse res = idempotencyService.execute(
                key,hash,
                () -> chatBotRoomService.saveNewChatBotRoom(passport.getKey(), title),
                ChatBotRoomResponse.class
        );
        return ResponseEntity.status(201).body(new ApiResponse<>(true,201,
                res));
    }


    @DeleteMapping("/room/{sessionId}")
    public ResponseEntity<ApiMessageResponse> deleteChatBotRoom(
            @PathVariable String sessionId
    ){
        chatBotRoomService.deleteChatBotRoom(sessionId);
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "챗봇방 삭제를 완료하였습니다."));
    }


    @PatchMapping("/room/{sessionId}/{title}")
    public ResponseEntity<ApiResponse<Boolean>> updateChatBotRoom(
            @PathVariable String sessionId, @PathVariable String title
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatBotRoomService.updateChatBotRoom(sessionId,title)));
    }
}
