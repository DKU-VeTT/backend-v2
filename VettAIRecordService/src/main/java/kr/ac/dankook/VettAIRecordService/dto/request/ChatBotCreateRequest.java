package kr.ac.dankook.VettAIRecordService.dto.request;

import kr.ac.dankook.VettAIRecordService.document.ChatBotRole;
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
