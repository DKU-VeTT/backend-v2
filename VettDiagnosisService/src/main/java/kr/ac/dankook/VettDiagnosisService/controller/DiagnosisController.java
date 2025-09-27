package kr.ac.dankook.VettDiagnosisService.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettDiagnosisService.dto.request.DiagnosisResultRequest;
import kr.ac.dankook.VettDiagnosisService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettDiagnosisService.dto.response.ApiResponse;
import kr.ac.dankook.VettDiagnosisService.dto.response.DiagnosisResultResponse;
import kr.ac.dankook.VettDiagnosisService.entity.Passport;
import kr.ac.dankook.VettDiagnosisService.error.ErrorCode;
import kr.ac.dankook.VettDiagnosisService.error.exception.CustomException;
import kr.ac.dankook.VettDiagnosisService.facade.DiagnosisFacade;
import kr.ac.dankook.VettDiagnosisService.service.DiagnosisService;
import kr.ac.dankook.VettDiagnosisService.service.StorageService;
import kr.ac.dankook.VettDiagnosisService.util.DecryptId;
import kr.ac.dankook.VettDiagnosisService.util.PassportMember;
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
@RequestMapping("/api/v1/diagnosis")
public class DiagnosisController {

    private final DiagnosisFacade diagnosisFacade;
    private final DiagnosisService diagnosisService;
    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<ApiMessageResponse> saveDiagnosisResult(
            @RequestPart("file") List<MultipartFile> file,
            @RequestPart("data") @Valid DiagnosisResultRequest data,
            @PassportMember Passport passport
    ){
        if (file == null || file.size() != 2){
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAM);
        }
         diagnosisFacade.saveDiagnosisResult(file,data,passport.getKey());
        return ResponseEntity.status(201).body(new ApiMessageResponse(true,201,
                 "진단 결과를 성공적으로 저장 완료하였습니다."));
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
