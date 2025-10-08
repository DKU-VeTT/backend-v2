package kr.ac.dankook.VettAIRecordService.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DiagnosisCompensationEvent {
    private String imageId;
}
