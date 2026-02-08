package com.guesser.demo.exception;

import com.guesser.demo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("ResponseStatusException: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getReason(),
            ex.getStatusCode().value()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(GuesserException.class)
    public ResponseEntity<Map<String, Object>> handleGuesserException(GuesserException ex) {
        logger.error("GuesserException: {} - {}", ex.getErrorCode().getCode(), ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", ex.getErrorCode().getCode());
        response.put("message", ex.getMessage());
        response.put("status", ex.getHttpStatus().value());
        
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unhandled Exception: ", ex);
        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", ErrorCodes.INTERNAL_SERVER_ERROR.getCode());
        response.put("message", ErrorCodes.INTERNAL_SERVER_ERROR.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 