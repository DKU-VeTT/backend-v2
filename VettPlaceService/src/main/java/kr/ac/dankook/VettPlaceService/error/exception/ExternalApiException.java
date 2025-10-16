package kr.ac.dankook.VettPlaceService.error.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException{
    private final String cause;
    
    public ExternalApiException(String message,String cause){
        super(message);
        this.cause = cause;
    }
}
