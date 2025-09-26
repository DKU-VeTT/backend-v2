package kr.ac.dankook.VettDiagnosisService.repository;

import kr.ac.dankook.VettDiagnosisService.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long> {
    List<Diagnosis> findByMemberId(String memberId);
}
