package kr.ac.dankook.VettObservabilityService.log;

public enum LogMessage {
    EVENT_CONSUME_ERROR,
    CONSUME_PDLT_RECORD,
    CONSUME_DLT_RECORD,
    JSON_PROCESSING_ERROR,
    KAFKA_SEND_EXCEPTION,
    KAFKA_RETRY_SUCCESS,
}
