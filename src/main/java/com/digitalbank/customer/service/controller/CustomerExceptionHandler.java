package com.digitalbank.customer.service.controller;

import com.digitalbank.common.exception.ConflictException;
import com.digitalbank.common.exception.CustomerNotFoundException;
import com.digitalbank.common.exception.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class CustomerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
        log.error("Unhandled exception occurred", ex);
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConflictException.class})
    public final ResponseEntity<ErrorResponse> customerConflictException(Exception ex, WebRequest request) throws Exception {
        log.error("Conflict exception occurred", ex);
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({CustomerNotFoundException.class, EntityNotFoundException.class})
    public final ResponseEntity<ErrorResponse> customerNotFoundException(Exception ex, WebRequest request) throws Exception {
        log.error("Customer not found exception occurred", ex);
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataAccessException ex, WebRequest request) {
        log.error("Database error", ex);
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
