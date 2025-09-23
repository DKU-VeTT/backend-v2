package kr.ac.dankook.VettChatRoomService.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private final boolean success = false;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
