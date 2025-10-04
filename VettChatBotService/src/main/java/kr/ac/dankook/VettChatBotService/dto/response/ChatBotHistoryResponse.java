package kr.ac.dankook.VettChatBotService.dto.response;

import kr.ac.dankook.VettChatBotService.document.ChatBotHistory;
import kr.ac.dankook.VettChatBotService.document.ChatBotRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatBotHistoryResponse {

    private String sessionId;
    private ChatBotRole role;
    private String content;
    private LocalDateTime time;

    @Builder
    public ChatBotHistoryResponse(ChatBotHistory chatBotHistory) {
        this.sessionId = chatBotHistory.getSessionId();
        this.role = chatBotHistory.getRole();
        this.content = chatBotHistory.getContent();
        this.time = chatBotHistory.getTime();
    }
}
