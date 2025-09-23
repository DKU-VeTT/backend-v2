package kr.ac.dankook.VettAuthService.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
