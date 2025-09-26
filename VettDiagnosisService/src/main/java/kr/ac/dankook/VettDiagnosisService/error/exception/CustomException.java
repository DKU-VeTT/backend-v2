package kr.ac.dankook.VettDiagnosisService.error.exception;

import kr.ac.dankook.VettDiagnosisService.error.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
