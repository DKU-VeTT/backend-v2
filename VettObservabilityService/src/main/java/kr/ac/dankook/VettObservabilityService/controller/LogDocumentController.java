package kr.ac.dankook.VettObservabilityService.controller;

import kr.ac.dankook.VettObservabilityService.document.LogLevel;
import kr.ac.dankook.VettObservabilityService.dto.response.ApiResponse;
import kr.ac.dankook.VettObservabilityService.dto.response.LogResponse;
import kr.ac.dankook.VettObservabilityService.service.LogDocumentService;
import kr.ac.dankook.VettObservabilityService.util.AccessControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/observability/log")
public class LogDocumentController {

    private final LogDocumentService logDocumentService;

    @GetMapping("/level/{level}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiResponse<List<LogResponse>>> getLogsByLevel(@PathVariable LogLevel level){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                logDocumentService.findByLevel(level)));
    }

    @GetMapping("/service/{name}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiResponse<List<LogResponse>>> getLogsByService(@PathVariable String name){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                logDocumentService.findByServiceName(name)));
    }

    @GetMapping("/trace/{traceId}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiResponse<List<LogResponse>>> getLogsByTraceId(@PathVariable String traceId){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                logDocumentService.findByTraceId(traceId)));
    }

}
