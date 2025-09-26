package kr.ac.dankook.VettDiagnosisService.error.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message){ super(message); }
}
