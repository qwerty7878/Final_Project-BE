package com.highpass.runspot.common.exception.handler;

import com.highpass.runspot.common.exception.BaseException;
import com.highpass.runspot.common.exception.dto.ErrorResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(final BaseException e) {
        log.error("BaseException: {}", e.getExceptionType().getMessage());
        return ErrorResponse.toResponseEntity(e.getExceptionType());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(final IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(final MethodArgumentNotValidException e) {
        log.error("ValidationException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("message", "요청 값이 올바르지 않습니다."));
    }
}