package kr.ac.dankook.VettObservabilityService.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettObservabilityService.document.EventStatus;
import kr.ac.dankook.VettObservabilityService.dto.request.UpdateEventRequest;
import kr.ac.dankook.VettObservabilityService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettObservabilityService.dto.response.ApiResponse;
import kr.ac.dankook.VettObservabilityService.dto.response.EventResponse;
import kr.ac.dankook.VettObservabilityService.service.EventDocumentService;
import kr.ac.dankook.VettObservabilityService.service.IdempotencyService;
import kr.ac.dankook.VettObservabilityService.util.AccessControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/observability/events")
public class EventDocumentController {

    private final EventDocumentService eventDocumentService;
    private final IdempotencyService idempotencyService;

    @GetMapping
    @AccessControl("ADMIN")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents(){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                eventDocumentService.getAllFailedEventList()));
    }

    @GetMapping("/{status}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByStatus(
            @PathVariable EventStatus status){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                eventDocumentService.getFailedEventByStatus(status)));
    }

    @PostMapping("/{id}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiMessageResponse> rePublishEvent(
            @PathVariable String id, @RequestHeader("Idempotency-Key") String key){
        
        idempotencyService.execute(key, () -> {
            eventDocumentService.retryPublishEvent(id);
            return null;
        });

        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "이벤트를 재발행 하였습니다."));
    }

    @DeleteMapping("/{id}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiMessageResponse> deleteEvent(@PathVariable String id){
        eventDocumentService.deleteEvent(id);
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "이벤트를 삭제하였습니다."));
    }

    @PatchMapping("/{id}")
    @AccessControl("ADMIN")
    public ResponseEntity<ApiMessageResponse> updateEvent(@PathVariable String id, @RequestBody @Valid UpdateEventRequest updateEventRequest){
        eventDocumentService.updateFailedEvent(id,updateEventRequest);
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "이벤트 상태 및 정보를 변경하였습니다."));
    }
}
