package kr.ac.dankook.VettAuthService.error;

import brave.http.HttpServerRequest;
import jakarta.servlet.http.HttpServletRequest;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettAuthService.log.LogError;
import kr.ac.dankook.VettAuthService.log.LogErrorConverter;
import kr.ac.dankook.VettAuthService.log.LogMessage;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e,  HttpServletRequest req) {
        ErrorCode errorCode = e.getErrorCode();

        String uri = req.getRequestURI();
        String className = e.getClassName();
        String methodName = e.getMethodName();
        String userKey = (String) req.getAttribute("userKey");
        String resultKey = Objects.requireNonNullElse(userKey, "NOT_USER");

        log.error(
                "[{}, uri={}, class={}, method={}, userKey={}, error={}, detailError={}]",
                LogMessage.CUSTOM_EXCEPTION,uri,className,methodName,resultKey,e.getMessage(),e.getDetailMessage());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getCode(),errorCode.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req) {

        String uri = req.getRequestURI();
        String className = e.getClassName();
        String methodName = e.getMethodName();
        String userKey = (String) req.getAttribute("userKey");
        String resultKey = Objects.requireNonNullElse(userKey, "NOT_USER");

        log.error(
                "[{}, uri={}, class={}, method={}, userKey={}, error={}]",
                LogMessage.ENTITY_NOT_FOUND, uri, className, methodName, resultKey, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("E001", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e, HttpServletRequest req) {

        LogError le = LogErrorConverter.convertToLogError(e,req);

        log.error(
                "[{}, uri={}, class={}, method={}, userKey={}, error={}]",
                LogMessage.ILLEGAL_STATE, le.getUri(), le.getClassName(), le.getMethodName(), le.getUserKey(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("E002", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req) {

        LogError le = LogErrorConverter.convertToLogError(e,req);

        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        String errorMessages = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        log.error(
                "[{}, uri={}, class={}, method={}, userKey={}, error={}]",
                LogMessage.METHOD_ARGUMENT_NOT_VALID, le.getUri(), le.getClassName(), le.getMethodName(),
                le.getUserKey(),
                errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("E003",errorMessages));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e,HttpServletRequest req){

        LogError le = LogErrorConverter.convertToLogError(e,req);

        log.error(
                "[{}, uri={}, class={}, method={}, userKey={}, error={}]",
                LogMessage.UNEXPECTED_EXCEPTION, le.getUri(), le.getClassName(), le.getMethodName(), le.getUserKey(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("E999",e.getMessage()));
    }

}
