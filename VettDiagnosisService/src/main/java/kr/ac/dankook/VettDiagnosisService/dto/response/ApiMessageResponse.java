package kr.ac.dankook.VettDiagnosisService.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
