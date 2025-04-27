package com.renewsim.backend.exception;

import com.renewsim.backend.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String traceId,
            Map<String, ?> details) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details != null ? (Map<String, String>) details : Map.of("traceId", traceId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        String traceId = generateTraceId();
        logger.error("TraceId: {} - Internal Server Error: {}", traceId, ex.getMessage(), ex);

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", traceId, null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String traceId = generateTraceId();
        logger.warn("TraceId: {} - Resource not found: {}", traceId, ex.getMessage());

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), traceId, null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        String traceId = generateTraceId();
        logger.warn("TraceId: {} - Bad Request: {}", traceId, ex.getMessage());

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), traceId, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = generateTraceId();
        logger.warn("TraceId: {} - Validation error: {}", traceId, ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, "One or more fields are invalid.", traceId, errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String traceId = generateTraceId();
        logger.warn("TraceId: {} - Validation failed: {}", traceId, ex.getMessage());

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), traceId, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        String traceId = generateTraceId();
        logger.warn("TraceId: {} - Unauthorized access attempt: {}", traceId, ex.getMessage());

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), traceId, null),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        String traceId = generateTraceId();
        logger.warn("TraceId: {} - Estado no permitido: {}", traceId, ex.getMessage());

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), traceId, null),
                HttpStatus.BAD_REQUEST);
    }

}
