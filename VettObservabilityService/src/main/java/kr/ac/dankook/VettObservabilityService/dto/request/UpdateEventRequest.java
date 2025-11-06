package kr.ac.dankook.VettObservabilityService.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.dankook.VettObservabilityService.document.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    @NotBlank(message = "토픽을 필수 입력 사항입니다.")
    @Size(max = 100, message = "토픽의 최대 길이는 100글자 입니다.")
    private String topic;

    @NotBlank(message = "파티션 키가 필요합니다.")
    private String partitionKey;

    @NotNull(message = "페이로드는 필수 입력 사항입니다.")
    private JsonNode payload;

    @NotNull(message = "이벤트 상태 정의가 필요합니다.")
    private EventStatus status;
}
