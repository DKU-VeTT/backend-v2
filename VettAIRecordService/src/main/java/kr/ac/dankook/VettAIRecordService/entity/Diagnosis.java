package kr.ac.dankook.VettAIRecordService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diagnosis extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DiagnosisType diagnosisType;
    private String description;
    private String originalImageId;
    private String diseaseName;
    @Enumerated(EnumType.STRING)
    private DiagnosisSecurityLevel severityLevel;
    private double confidenceScore;
    private String memberId;

    @Column(columnDefinition = "TEXT")
    private String diagnosisResult;

    @Builder
    public Diagnosis(DiagnosisType diagnosisType, String description, String originalImageId,
                     String diseaseName, DiagnosisSecurityLevel severityLevel,
                     double confidenceScore, String diagnosisResult,String memberId) {
        this.diagnosisType = diagnosisType;
        this.description = description;
        this.originalImageId = originalImageId;
        this.diseaseName = diseaseName;
        this.severityLevel = severityLevel;
        this.confidenceScore = confidenceScore;
        this.diagnosisResult = diagnosisResult;
        this.memberId = memberId;
    }
}
