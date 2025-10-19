package kr.ac.dankook.VettCloudGatewayService.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String className;
    private final String methodName;
    private String detailMessage = "NONE";

    public CustomException(ErrorCode errorCode, String className, String methodName) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.className = className;
        this.methodName = methodName;
    }

    public CustomException(ErrorCode errorCode, String className, String methodName,String detailMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.className = className;
        this.methodName = methodName;
        this.detailMessage = detailMessage;
    }
}