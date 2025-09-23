package kr.ac.dankook.VettChatBotService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatBotRoomCreateRequest {

    @NotBlank(message = "챗봇방 이름은 필수입니다.")
    @Size(max = 50,min = 2)
    private String title;
}
