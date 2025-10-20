package kr.ac.dankook.VettChatService.facade;

import kr.ac.dankook.VettChatService.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.service.ChatRoomJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomJoinFacade {

    private final ChatRoomJoinService chatRoomJoinService;
    private static final int RETRY_DELAY_MS = 50;
    private static final int MAX_RETRY_COUNT = 20;

    public ChatRoomResponse joinChatRoom(Long chatRoomId, String nickname, String memberId) throws InterruptedException {

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        int retryCount = 0;
        while(retryCount < MAX_RETRY_COUNT) {
            try{
                return chatRoomJoinService.joinChatRoom(chatRoomId,nickname, memberId);
            }catch (Exception e){
                if (e instanceof CustomException customException){
                    throw new CustomException(customException.getErrorCode(),className,methodName);
                }
                retryCount++;
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new CustomException(ErrorCode.DELAY_JOIN_CHATROOM,className,methodName);
    }

    public void leaveChatRoom(Long chatRoomId, String memberId) throws InterruptedException {

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        int retryCount = 0;
        while(retryCount < MAX_RETRY_COUNT) {
            try{
                chatRoomJoinService.leaveChatRoom(chatRoomId, memberId);
                return;
            }catch (Exception e){
                if (e instanceof CustomException customException){
                    throw new CustomException(customException.getErrorCode(),className,methodName);
                }
                retryCount++;
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new CustomException(ErrorCode.DELAY_LEAVE_CHATROOM,className,methodName);
    }
}
