package kr.ac.dankook.VettPlaceService.error.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException{

    private final String className;
    private final String methodName;
    private String detailMessage = "NONE";

    public ExternalApiException(String message,String className, String methodName,String detailMessage){
        super(message);
        this.className = className;
        this.methodName = methodName;
        this.detailMessage = detailMessage;
    }
}
