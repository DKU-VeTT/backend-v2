package kr.ac.dankook.VettObservabilityService.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
