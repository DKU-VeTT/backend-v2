package kr.ac.dankook.VettChatService.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettChatService.dto.request.ChatRoomCreateRequest;
import kr.ac.dankook.VettChatService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettChatService.dto.response.ApiResponse;
import kr.ac.dankook.VettChatService.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatService.entity.Passport;
import kr.ac.dankook.VettChatService.facade.ChatRoomJoinFacade;
import kr.ac.dankook.VettChatService.service.ChatRoomJoinService;
import kr.ac.dankook.VettChatService.service.ChatRoomPinService;
import kr.ac.dankook.VettChatService.service.ChatRoomService;
import kr.ac.dankook.VettChatService.util.DecryptId;
import kr.ac.dankook.VettChatService.util.PassportMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatRooms")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomJoinFacade chatRoomJoinFacade;
    private final ChatRoomJoinService chatRoomJoinService;
    private final ChatRoomPinService chatRoomPinService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyChatRooms(
            @PassportMember Passport passport
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatRoomService.getMyChatRoomList(passport.getKey())));
    }

    @GetMapping("/owner")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyOwnerRooms(
            @PassportMember Passport passport
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatRoomService.getMyManagerChatRoomList(passport.getKey())));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createNewChatRoom(
            @RequestBody @Valid ChatRoomCreateRequest request,
            @PassportMember Passport passport
    )
    {
        return ResponseEntity.status(201).body(new ApiResponse<>(true,201,
                chatRoomService.saveNewChatRoom(passport.getKey(),request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getAllChatRooms() {
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatRoomService.getAllChatRoomList()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> searchChatRooms(
            @RequestParam("keyword") String keyword
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatRoomService.getChatRoomListByKeyword(keyword)));
    }

    @PostMapping("/pin/{roomId}")
    public ResponseEntity<ApiResponse<Boolean>> togglePin(
            @PathVariable @DecryptId Long roomId,
            @PassportMember Passport passport
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
               chatRoomPinService.toggleChatRoomPin(roomId,passport.getKey())));
    }

    @GetMapping("/join/{roomId}")
    public ResponseEntity<ApiResponse<Boolean>> isJoinChatRoom(
            @PathVariable @DecryptId Long roomId,
            @PassportMember Passport passport
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                chatRoomJoinService.isJoinChatRoom(roomId, passport.getKey())));
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> joinChatRoom(
            @PathVariable @DecryptId Long roomId,
            @RequestParam("nickname") String nickname,
            @PassportMember Passport passport
    ) throws InterruptedException {
        return ResponseEntity.status(200).body(new ApiResponse<>(true,201,
                chatRoomJoinFacade.joinChatRoom(roomId,nickname,passport.getKey())));
    }

    @DeleteMapping("/join/{roomId}")
    public ResponseEntity<ApiMessageResponse> leaveChatRoom(
            @PathVariable @DecryptId Long roomId,
            @PassportMember Passport passport
    ) throws InterruptedException {
        chatRoomJoinFacade.leaveChatRoom(roomId, passport.getKey());
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "채팅방을 성공적으로 나갔습니다."));
    }


    @PatchMapping("/read-count/{roomId}")
    public ResponseEntity<ApiMessageResponse> clearUnreadMessages(
            @PathVariable @DecryptId Long roomId,
            @PassportMember Passport passport
    ){
        chatRoomService.updateUnreadMessages(roomId,passport.getKey());
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "읽지 않은 메시지를 초기화하였습니다."));
    }
}
