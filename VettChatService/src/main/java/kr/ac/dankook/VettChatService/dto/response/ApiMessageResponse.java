package kr.ac.dankook.VettChatService.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
