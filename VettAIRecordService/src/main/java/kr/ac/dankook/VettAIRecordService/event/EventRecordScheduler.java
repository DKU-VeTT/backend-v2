package kr.ac.dankook.VettAIRecordService.event;

import jakarta.persistence.EntityManager;
import kr.ac.dankook.VettAIRecordService.entity.EventRecord;
import kr.ac.dankook.VettAIRecordService.repository.EventRecordRepository;
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

    // 지수 백오프 전략을 기준으로 최대 5번의 재시도 동안 컨슈머가 총 대기하는 시간은 대략 3.1초이다.
    // 만약 각 로직을 처리하는데 10초 정도가 걸리고 최대 5번의 재시도를 하였다면 50초가 소요된다.
    // 따라서 스케줄러에서 레코드의 생성 시간과 현재 시간을 비교하여 90초 이상일 경우에만 삭제를 진행한다.
    // 한 번의 재시도에서 실패할 경우 롤백되어 레코드가 삭제되므로, 대기 시간 + 실행 시간 정도의 차이만 스케줄러에서 보장하면 되지만,
    // 안전하게 처리하기 위해 90초 정도의 여유시간을 채택
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
