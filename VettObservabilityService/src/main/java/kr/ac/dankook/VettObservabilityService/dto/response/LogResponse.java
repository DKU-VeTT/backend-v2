package kr.ac.dankook.VettObservabilityService.dto.response;

import kr.ac.dankook.VettObservabilityService.document.LogDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogResponse {

    private String id;
    private String timestamp;
    private String level;
    private int levelValue;
    private String service;
    private String app;
    private String threadName;
    private String message;
    private String traceId;
    private String spanId;

    public LogResponse(LogDocument logDocument){
        this.id = logDocument.getId();
        this.timestamp = logDocument.getTimestamp();
        this.level = logDocument.getLevel();
        this.levelValue = logDocument.getLevelValue();
        this.service = logDocument.getService();
        this.app = logDocument.getApp();
        this.threadName = logDocument.getThreadName();
        this.message = logDocument.getMessage();
        this.traceId = logDocument.getTraceId();
        this.spanId = logDocument.getSpanId();
    }
}
