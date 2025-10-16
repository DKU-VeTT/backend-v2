package kr.ac.dankook.VettAIRecordService.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettAIRecordService.dto.request.DiagnosisResultRequest;
import kr.ac.dankook.VettAIRecordService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettAIRecordService.dto.response.ApiResponse;
import kr.ac.dankook.VettAIRecordService.dto.response.DiagnosisResultResponse;
import kr.ac.dankook.VettAIRecordService.entity.Passport;
import kr.ac.dankook.VettAIRecordService.error.ErrorCode;
import kr.ac.dankook.VettAIRecordService.error.exception.CustomException;
import kr.ac.dankook.VettAIRecordService.facade.DiagnosisFacade;
import kr.ac.dankook.VettAIRecordService.service.DiagnosisService;
import kr.ac.dankook.VettAIRecordService.service.IdempotencyService;
import kr.ac.dankook.VettAIRecordService.service.StorageService;
import kr.ac.dankook.VettAIRecordService.util.DecryptId;
import kr.ac.dankook.VettAIRecordService.util.PassportMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/ai/record/diagnosis")
public class DiagnosisController {

    private final DiagnosisFacade diagnosisFacade;
    private final DiagnosisService diagnosisService;
    private final StorageService storageService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    public ResponseEntity<ApiMessageResponse> saveDiagnosisResult(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") @Valid DiagnosisResultRequest data,
            @RequestHeader("Idempotency-Key") String key,
            @PassportMember Passport passport
    ){
        if (file == null){
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAM);
        }
        String res = idempotencyService.execute(
                key,
                () -> diagnosisFacade.saveDiagnosisResult(file,data,passport.getKey()),
                String.class
        );
        diagnosisFacade.saveDiagnosisResult(file,data,passport.getKey());
        return ResponseEntity.status(201).body(new ApiMessageResponse(true,201,
                res));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteDiagnosisResult(@PathVariable @DecryptId Long id) {
        diagnosisFacade.deleteDiagnosisResult(id);
        return ResponseEntity.status(201).body(new ApiMessageResponse(true,201,
                "진단 결과를 성공적으로 삭제하였습니다."));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<DiagnosisResultResponse>>> getDiagnosisResults(@PassportMember Passport passport){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                diagnosisService.getDiagnosisResults(passport.getKey())));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<Resource> getImageFile(@PathVariable String id){
        var res = storageService.getFile(new ObjectId(id));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(res.getContentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .eTag(res.getFilename())
                .body(res);
    }
}
