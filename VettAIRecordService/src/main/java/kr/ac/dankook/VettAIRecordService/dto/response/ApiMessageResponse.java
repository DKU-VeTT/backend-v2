package kr.ac.dankook.VettAIRecordService.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
