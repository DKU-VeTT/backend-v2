package kr.ac.dankook.VettAIRecordService.repository;

import kr.ac.dankook.VettAIRecordService.entity.EventRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRecordRepository extends JpaRepository<EventRecord,String> {

    @Modifying
    @Query(value = """
        INSERT INTO event_record(event_id, created_date_time, modified_date_time)
        VALUES (:id, NOW(), NOW())
        ON DUPLICATE KEY UPDATE event_id = event_record.event_id
        """, nativeQuery = true)
    void upsert(@Param("id") String id);

    @Query(value = """
        SELECT * FROM event_record
        WHERE created_date_time < DATE_SUB(NOW(), INTERVAL :seconds SECOND)
        """, nativeQuery = true)
    List<EventRecord> findOldRecordsByTimestamp(@Param("seconds") int seconds);

}