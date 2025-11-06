package kr.ac.dankook.VettAIRecordService.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OutboxEvent {
    private String id;
    private String eventDomain;
    private String eventTopic;
    private String userKey;
}
