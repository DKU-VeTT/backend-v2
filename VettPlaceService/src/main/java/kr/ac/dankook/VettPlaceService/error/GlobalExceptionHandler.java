package kr.ac.dankook.VettPlaceService.error;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.dankook.VettPlaceService.error.exception.CustomException;
import kr.ac.dankook.VettPlaceService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettPlaceService.error.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest req) {

        ErrorCode errorCode = e.getErrorCode();
        log.error(
                "[custom_exception, component={}, uri={}, error={}]",
                "GlobalExceptionHandler",req.getRequestURI(),e.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getCode(),errorCode.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e,HttpServletRequest req) {
        log.error(
                "[entity_not_found_exception, component={}, uri={}, error={}]",
                "GlobalExceptionHandler",req.getRequestURI(),e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("E001", e.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e, HttpServletRequest req) {
        log.error(
                "[external_api_exception, component={}, uri={}, error={} cause={}]",
                "GlobalExceptionHandler",req.getRequestURI(),e.getMessage(),e.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("E004",e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e,HttpServletRequest req) {
        log.error(
                "[Illegal_state_exception, component={}, uri={}, error={}]",
                "GlobalExceptionHandler",req.getRequestURI(),e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("E002", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        String errorMessages = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        log.error(
                "[method_argument_exception, component={}, uri={}, error={}]",
                "GlobalExceptionHandler",req.getRequestURI(),e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("E003",errorMessages));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest req){
        log.error(
                "[unexpected_exception, component={}, uri={}, error={}]",
                "GlobalExceptionHandler",req.getRequestURI(),e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("E999",e.getMessage()));
    }
}
