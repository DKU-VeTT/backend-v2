package kr.ac.dankook.VettPlaceService.error.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException{

    private final String className;
    private final String methodName;

    public EntityNotFoundException(String message,String className, String methodName){
        super(message);
        this.className = className;
        this.methodName = methodName;
    }
}
