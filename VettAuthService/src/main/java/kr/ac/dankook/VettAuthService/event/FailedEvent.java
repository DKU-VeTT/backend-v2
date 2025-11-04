package kr.ac.dankook.VettAuthService.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Builder
public class FailedEvent {
    private String serviceName;
    private String className;
    private String originalTopic;
    private String partitionKey;
    private String errorMessage;
    private Map<String,Object> payload;
}
