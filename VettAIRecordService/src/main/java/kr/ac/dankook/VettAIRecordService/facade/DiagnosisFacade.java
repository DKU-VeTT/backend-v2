package kr.ac.dankook.VettAIRecordService.facade;

import kr.ac.dankook.VettAIRecordService.dto.request.DiagnosisResultRequest;
import kr.ac.dankook.VettAIRecordService.entity.Diagnosis;
import kr.ac.dankook.VettAIRecordService.service.DiagnosisService;
import kr.ac.dankook.VettAIRecordService.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiagnosisFacade {

    private final DiagnosisService diagnosisService;
    private final StorageService storageService;

    public String saveDiagnosisResult(MultipartFile file, DiagnosisResultRequest data, String memberId){
        String imageId = storageService.uploadFile(file);
        diagnosisService.saveDiagnosisEntity(imageId,memberId,data);
        return "진단 결과를 성공적으로 저장 완료하였습니다.";
    }

    public void deleteDiagnosisResult(Long id){
        Diagnosis diagnosis = diagnosisService.getDiagnosisById(id);
        storageService.deleteFile(new ObjectId(diagnosis.getOriginalImageId()));
        diagnosisService.deleteDiagnosisEntity(id);
    }
}
