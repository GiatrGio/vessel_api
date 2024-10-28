package com.example.vessel_api.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> handleApplicationException(ApplicationException ex) {
        Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.warn("Application Exception: {}", ex.getMessage());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getMessage());
    }
}
