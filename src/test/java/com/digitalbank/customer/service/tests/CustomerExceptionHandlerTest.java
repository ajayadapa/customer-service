package com.digitalbank.customer.service.tests;

import com.digitalbank.customer.service.controller.CustomerExceptionHandler;
import com.digitalbank.common.exception.ConflictException;
import com.digitalbank.common.exception.CustomerNotFoundException;
import com.digitalbank.common.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomerExceptionHandlerTest {

    private CustomerExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new CustomerExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("test-request");
    }

    @Test
    void handleAllExceptions_shouldReturnInternalServerError() throws Exception {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleAllExceptions(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Something went wrong", response.getBody().getMessage());
    }

    @Test
    void customerConflictException_shouldReturnConflict() throws Exception {
        ConflictException ex = new ConflictException("Customer already exists");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.customerConflictException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Customer already exists", response.getBody().getMessage());
    }

    @Test
    void customerNotFoundException_shouldReturnNotFound() throws Exception {
        CustomerNotFoundException ex = new CustomerNotFoundException("Customer not found");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.customerNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Customer not found", response.getBody().getMessage());
    }
}

