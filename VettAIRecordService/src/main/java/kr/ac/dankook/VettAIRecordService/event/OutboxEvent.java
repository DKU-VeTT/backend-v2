package kr.ac.dankook.VettAIRecordService.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OutboxEvent {

    private String id;
    private String eventDomain;
    private String eventType;
    @JsonProperty("payload")
    private Map<String,Object> payload;
}
