package kr.ac.dankook.VettAIRecordService.facade;

import kr.ac.dankook.VettAIRecordService.dto.request.DiagnosisResultRequest;
import kr.ac.dankook.VettAIRecordService.entity.Diagnosis;
import kr.ac.dankook.VettAIRecordService.service.DiagnosisService;
import kr.ac.dankook.VettAIRecordService.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiagnosisFacade {

    private final DiagnosisService diagnosisService;
    private final StorageService storageService;

    public void saveDiagnosisResult(List<MultipartFile> file, DiagnosisResultRequest data, String memberId){
        List<String> ids = storageService.uploadFilesAndGetIds(file);
        diagnosisService.saveDiagnosisEntity(ids,memberId,data);
    }

    public void deleteDiagnosisResult(Long id){
        Diagnosis diagnosis = diagnosisService.getDiagnosisById(id);
        storageService.deleteFiles(diagnosis.getImageObjectIds());
        diagnosisService.deleteDiagnosisEntity(id);
    }
}
