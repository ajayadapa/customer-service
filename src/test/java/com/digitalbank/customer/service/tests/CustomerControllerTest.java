package com.digitalbank.customer.service.tests;

import com.digitalbank.customer.service.controller.CustomerController;
import com.digitalbank.customer.service.dto.*;
import com.digitalbank.customer.service.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void createCustomer_success() {
        CustomerCreationRequest request = new CustomerCreationRequest();
        CustomerCreatedResponse response = new CustomerCreatedResponse();
        when(customerService.createCustomer(any(CustomerCreationRequest.class))).thenReturn(response);
        ResponseEntity<CustomerCreatedResponse> result = customerController.createCustomer(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(customerService).createCustomer(request);
    }

    @Test
    void getCustomerByUserId_success() {
        CustomerResponse response = new CustomerResponse();
        when(customerService.getCustomerByUserId("user-1")).thenReturn(response);
        ResponseEntity<CustomerResponse> result = customerController.getCustomerByUserId("user-1");
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(customerService).getCustomerByUserId("user-1");
    }

    @Test
    void updateCustomer_success() {
        UpdateCustomerRequest request = new UpdateCustomerRequest();
        CustomerResponse response = new CustomerResponse();
        when(customerService.updateCustomer(any(UpdateCustomerRequest.class))).thenReturn(response);
        ResponseEntity<CustomerResponse> result = customerController.updateCustomer(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(customerService).updateCustomer( request);
    }

    @Test
    void updateKycStatus_success() {
        when(customerService.updateKycStatus("user-1", "VERIFIED")).thenReturn("KYC UPDATED");
        UpdateKycRequest request = new UpdateKycRequest("user-1", "VERIFIED");
        ResponseEntity<String> result = customerController.updateKycStatus(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("KYC UPDATED", result.getBody());
        verify(customerService).updateKycStatus("user-1", "VERIFIED");
    }

    @Test
    void getCustomerByEmail_success() {
        when(customerService.getCustomerByEmail("test@mail.com")).thenReturn(true);
        ResponseEntity<Boolean> result = customerController.getCustomerByEmail("test@mail.com");
        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody());
        verify(customerService).getCustomerByEmail("test@mail.com");
    }

    @Test
    void getCustomerByPhone_success() {
        when(customerService.getCustomerByPhone("9999999999")).thenReturn(false);
        ResponseEntity<Boolean> result = customerController.getCustomerByPhone("9999999999");
        assertEquals(200, result.getStatusCode().value());
        assertFalse(result.getBody());
        verify(customerService).getCustomerByPhone("9999999999");
    }
}
