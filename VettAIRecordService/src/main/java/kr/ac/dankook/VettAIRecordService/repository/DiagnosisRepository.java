package kr.ac.dankook.VettAIRecordService.repository;

import kr.ac.dankook.VettAIRecordService.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long> {
    List<Diagnosis> findByMemberId(String memberId);
}
