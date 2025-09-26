package kr.ac.dankook.VettDiagnosisService.service;

import kr.ac.dankook.VettDiagnosisService.dto.request.DiagnosisResultRequest;
import kr.ac.dankook.VettDiagnosisService.dto.response.DiagnosisResultResponse;
import kr.ac.dankook.VettDiagnosisService.entity.Diagnosis;
import kr.ac.dankook.VettDiagnosisService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettDiagnosisService.event.DiagnosisCompensationEvent;
import kr.ac.dankook.VettDiagnosisService.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Diagnosis getDiagnosisById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("진단 결과가 존재하지 않습니다."));
    }

    public List<DiagnosisResultResponse> getDiagnosisResults(String memberId){
        return diagnosisRepository.findByMemberId(memberId)
                .stream().map(DiagnosisResultResponse::new).toList();
    }

    @Transactional
    public void saveDiagnosisEntity(List<String> ids, String memberId, DiagnosisResultRequest data){
        Diagnosis newDiagnosis = Diagnosis.builder()
                .memberId(memberId)
                .originalImageId(ids.get(0))
                .resultImageId(ids.get(1))
                .diagnosisType(data.getDiagnosisType())
                .diagnosisResult(data.getDiagnosisResult())
                .description(data.getDescription())
                .confidenceScore(data.getConfidenceScore())
                .severityLevel(data.getSeverityLevel())
                .diseaseName(data.getDiseaseName()).build();
        diagnosisRepository.save(newDiagnosis);
        eventPublisher.publishEvent(new DiagnosisCompensationEvent(ids));
    }

    public void deleteDiagnosisEntity(Long id){
        diagnosisRepository.deleteById(id);
    }
}
