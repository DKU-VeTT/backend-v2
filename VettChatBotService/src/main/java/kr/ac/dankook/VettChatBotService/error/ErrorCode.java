package kr.ac.dankook.VettChatBotService.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 인증관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"A001","인증되지 않은 접근입니다."),
    // 권한관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN,"P001","접근 권한이 존재하지 않습니다."),

    // 입력값 검증 에러
    INVALID_ENCRYPT_PK(HttpStatus.BAD_REQUEST,"V001", "유효하지 않은 데이터 아이디 형식입니다."),
    INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST,"V002","필수 입력 항목이 누락되었습니다."),
    // 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"S001","서버 내부 오류가 발생하였습니다."),
    JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"S002", "내부 객체 변형과정에서 에러가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
