package com.example.news_management_service.exception;

import com.example.news_management_service.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the NewsException, which is thrown for news-related errors.
     *
     * @param ex The NewsException that was thrown.
     * @return A ResponseEntity containing the error details and HTTP status.
     */
    @ExceptionHandler(NewsException.class)
    public ResponseEntity<?> handleNewsException(NewsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
