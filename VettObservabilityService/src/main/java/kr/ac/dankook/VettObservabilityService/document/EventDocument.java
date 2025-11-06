package kr.ac.dankook.VettObservabilityService.document;

import kr.ac.dankook.VettObservabilityService.dto.request.UpdateEventRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "event_document")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDocument {

    @Id
    private String id;
    @Field("service_name")
    private String serviceName;
    @Field("error_class")
    private String errorClass;
    @Field("error_message")
    private String errorMessage;
    private String topic;
    private String partitionKey;
    private String payload;
    private String status;
    private LocalDateTime timestamp;

    @Builder
    public EventDocument(String serviceName, String errorClass, String errorMessage,
                         String topic, String partitionKey, String payload, EventStatus status){
        this.serviceName = serviceName;
        this.errorClass = errorClass;
        this.errorMessage = errorMessage;
        this.topic = topic;
        this.partitionKey = partitionKey;
        this.payload = payload;
        this.status = status.toString();
        this.timestamp = LocalDateTime.now();
    }

    public void convertStatus(EventStatus status){
        this.status = status.toString();
    }

    public void updateEvent(String topic,String partitionKey, String payload, EventStatus status){
        this.topic = topic;
        this.partitionKey = partitionKey;
        this.payload = payload;
        this.status = status.toString();
    }
}
