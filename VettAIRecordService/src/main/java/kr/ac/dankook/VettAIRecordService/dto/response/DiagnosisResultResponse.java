package kr.ac.dankook.VettAIRecordService.dto.response;

import kr.ac.dankook.VettAIRecordService.entity.Diagnosis;
import kr.ac.dankook.VettAIRecordService.entity.DiagnosisSecurityLevel;
import kr.ac.dankook.VettAIRecordService.entity.DiagnosisType;
import kr.ac.dankook.VettAIRecordService.util.EncryptionUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DiagnosisResultResponse {

    private String id;
    private DiagnosisType diagnosisType;
    private String description;
    private String originalImageId;
    private String resultImageId;
    private String diseaseName;
    private DiagnosisSecurityLevel severityLevel;
    private double confidenceScore;
    private String diagnosisResult;
    private LocalDateTime time;

    public DiagnosisResultResponse(Diagnosis diagnosis) {
        this.id = EncryptionUtil.encrypt(diagnosis.getId());
        this.diagnosisType = diagnosis.getDiagnosisType();
        this.description = diagnosis.getDescription();
        this.originalImageId = diagnosis.getOriginalImageId();
        this.diseaseName = diagnosis.getDiseaseName();
        this.severityLevel = diagnosis.getSeverityLevel();
        this.confidenceScore = diagnosis.getConfidenceScore();
        this.diagnosisResult = diagnosis.getDiagnosisResult();
        this.time = diagnosis.getCreatedDateTime();
    }
}
