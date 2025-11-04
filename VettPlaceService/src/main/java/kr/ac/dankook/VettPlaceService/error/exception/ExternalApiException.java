package kr.ac.dankook.VettPlaceService.error.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException{

    public ExternalApiException(String message){
        super(message);
    }
}
