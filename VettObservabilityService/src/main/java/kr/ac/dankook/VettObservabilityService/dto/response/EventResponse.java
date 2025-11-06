package kr.ac.dankook.VettObservabilityService.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import kr.ac.dankook.VettObservabilityService.document.EventDocument;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventResponse {

    private String id;
    private String serviceName;
    private String errorClass;
    private String errorMessage;
    private String topic;
    private String partitionKey;
    @JsonRawValue
    private String payload;
    private String status;
    private LocalDateTime timestamp;

    public EventResponse(EventDocument eventDocument){
        this.id = eventDocument.getId();
        this.serviceName = eventDocument.getServiceName();
        this.errorClass = eventDocument.getErrorClass();
        this.errorMessage = eventDocument.getErrorMessage();
        this.topic = eventDocument.getTopic();
        this.partitionKey = eventDocument.getPartitionKey();
        this.payload = eventDocument.getPayload();
        this.status = eventDocument.getStatus();
        this.timestamp = eventDocument.getTimestamp();
    }
}
