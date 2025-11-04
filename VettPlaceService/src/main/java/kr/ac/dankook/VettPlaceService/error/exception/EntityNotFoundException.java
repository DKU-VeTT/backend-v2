package kr.ac.dankook.VettPlaceService.error.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String message){
        super(message);
    }
}
