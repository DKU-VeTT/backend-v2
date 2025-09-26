package kr.ac.dankook.VettDiagnosisService.entity;

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
    private DiagnosisType diagnosisType;
    private String description;
    private String originalImageId;
    private String resultImageId;
    private String diseaseName;
    private DiagnosisSecurityLevel severityLevel;
    private double confidenceScore;
    private String memberId;

    @Column(columnDefinition = "TEXT")
    private String diagnosisResult;

    @Builder
    public Diagnosis(DiagnosisType diagnosisType, String description, String originalImageId,
                     String resultImageId,  String diseaseName, DiagnosisSecurityLevel severityLevel,
                     double confidenceScore, String diagnosisResult,String memberId) {
        this.diagnosisType = diagnosisType;
        this.description = description;
        this.originalImageId = originalImageId;
        this.resultImageId = resultImageId;
        this.diseaseName = diseaseName;
        this.severityLevel = severityLevel;
        this.confidenceScore = confidenceScore;
        this.diagnosisResult = diagnosisResult;
        this.memberId = memberId;
    }

    public List<ObjectId> getImageObjectIds(){
        List<ObjectId> ids = new ArrayList<>();
        String originalImageId = this.getOriginalImageId();
        String diagnosisResultImageId = this.getResultImageId();
        ids.add(new ObjectId(originalImageId));
        ids.add(new ObjectId(diagnosisResultImageId));

        return ids;
    }
}
