package kr.ac.dankook.VettObservabilityService.event;

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
public class FailedEvent {
    private String serviceName;
    private String className;
    private String originalTopic;
    private String partitionKey;
    private String errorMessage;
    @JsonProperty("payload")
    private Map<String,Object> payload;
}
