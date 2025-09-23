package kr.ac.dankook.VettChatBotService.dto.request;

import kr.ac.dankook.VettChatBotService.document.ChatBotRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class ChatBotCreateRequest {

    private String sessionId;
    private String content;
    private ChatBotRole role;
}
