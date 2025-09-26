package kr.ac.dankook.VettDiagnosisService.dto.request;

import jakarta.validation.constraints.*;
import kr.ac.dankook.VettDiagnosisService.entity.DiagnosisSecurityLevel;
import kr.ac.dankook.VettDiagnosisService.entity.DiagnosisType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DiagnosisResultRequest {

    @NotNull
    private DiagnosisType diagnosisType;
    @Size(max = 50000)
    @NotBlank
    private String description;
    @NotBlank
    private String diseaseName;
    @NotNull
    private DiagnosisSecurityLevel severityLevel;
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double confidenceScore;
    @NotBlank
    private String diagnosisResult;
}
