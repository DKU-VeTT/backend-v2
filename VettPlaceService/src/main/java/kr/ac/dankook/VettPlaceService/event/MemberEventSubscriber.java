package kr.ac.dankook.VettPlaceService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import kr.ac.dankook.VettPlaceService.entity.Bookmark;
import kr.ac.dankook.VettPlaceService.log.LogMessage;
import kr.ac.dankook.VettPlaceService.repository.BookmarkRepository;
import kr.ac.dankook.VettPlaceService.repository.EventRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberEventSubscriber {

    private final ObjectMapper objectMapper;
    private final EventRecordRepository eventRecordRepository;
    private final BookmarkRepository bookmarkRepository;
    private final EntityManager entityManager;

    @KafkaListener(groupId = "VETT_PLACE", topics = "user.event.deleted")
    @Transactional
    public void consumeMemberDeleted(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment ack) throws JsonProcessingException {

        OutboxEvent event = objectMapper.readValue(payload, OutboxEvent.class);

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        String id = event.getId();
        if (eventRecordRepository.findById(id).isPresent()){
            log.info("[{}, class={}, method={}, eventId={}]",
                    LogMessage.MEMBER_EVENT_DUPLICATED_ACK_ONLY, className, methodName, LocalDateTime.now());
            ack.acknowledge();
            return;
        }
        eventRecordRepository.upsert(id);

        log.info("[{}, class={}, method={}, topic={}, partitionKey={}, payload={}]",
                LogMessage.MEMBER_EVENT_LISTENED, className, methodName, topic, partitionKey, payload);

        String memberId = (String) event.getPayload().get("userKey");
        entityManager.flush();
        List<Bookmark> bookmarks = bookmarkRepository.findByMemberId(memberId);
        bookmarkRepository.deleteAllInBatch(bookmarks);
        entityManager.clear();;

        ack.acknowledge();
    }
}
