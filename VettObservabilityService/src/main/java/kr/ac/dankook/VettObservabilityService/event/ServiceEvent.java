package kr.ac.dankook.VettObservabilityService.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.ac.dankook.VettObservabilityService.document.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServiceEvent {

    private String eventId;
    private String eventDomain;
    private String eventType;
    private String topic;
    private String partitionKey;
    @JsonProperty("payload")
    private Map<String,Object> payload;
    private String serviceName;
    private EventStatus status;
}
