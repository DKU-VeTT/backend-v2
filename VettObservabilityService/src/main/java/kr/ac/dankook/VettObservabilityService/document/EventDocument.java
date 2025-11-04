package kr.ac.dankook.VettObservabilityService.document;

import kr.ac.dankook.VettObservabilityService.event.ServiceEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalTime;

@Document(collection = "event_document")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDocument {

    @Id
    private String id;
    @Field("event_id")
    private String eventId;
    @Field("event_domain")
    private String eventDomain;
    @Field("event_type")
    private String eventType;
    @Field("partition_key")
    private String partitionKey;
    private String payload;
    private String status;
    private LocalTime time;
    private String serviceName;

    public EventDocument(ServiceEvent serviceEvent) {
        this.eventId = serviceEvent.getEventId();
        this.eventDomain = serviceEvent.getEventDomain();
        this.eventType = serviceEvent.getEventType();
        this.partitionKey = serviceEvent.getPartitionKey();
        // this.payload = serviceEvent.getPayload();
        this.status = serviceEvent.getStatus().toString();
        this.serviceName = serviceEvent.getServiceName();
        this.time = LocalTime.now();
    }
}
