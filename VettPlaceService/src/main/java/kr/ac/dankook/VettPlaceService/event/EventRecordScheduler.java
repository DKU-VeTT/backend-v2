package kr.ac.dankook.VettPlaceService.event;

import jakarta.persistence.EntityManager;
import kr.ac.dankook.VettPlaceService.entity.EventRecord;
import kr.ac.dankook.VettPlaceService.repository.EventRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventRecordScheduler {

    private final EventRecordRepository eventRecordRepository;
    private final EntityManager entityManager;

    private static final int CLEANUP_SECONDS = 90;

    @Scheduled(fixedDelay = 60000 * 10)
    @Transactional
    public void recordDeleteScheduler(){
        log.info(
                "[record_delete_scheduler, component={}, date={}]",
                "EventRecordScheduler", LocalDateTime.now());
        entityManager.flush();
        List<EventRecord> oldRecords = eventRecordRepository.findOldRecordsByTimestamp(CLEANUP_SECONDS);
        eventRecordRepository.deleteAllInBatch(oldRecords);
        entityManager.clear();
    }
}
