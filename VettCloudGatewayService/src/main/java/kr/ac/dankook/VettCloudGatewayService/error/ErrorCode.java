package kr.ac.dankook.VettCloudGatewayService.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"A001","인증되지 않은 접근입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"A002","유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"A003","만료된 토큰입니다."),
    PASSPORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"A004","Passport 발급에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"G001","게이트웨이 서버에서 오류가 발생하였습니다."),
    JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"G002", "내부 객체 변형과정에서 에러가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}