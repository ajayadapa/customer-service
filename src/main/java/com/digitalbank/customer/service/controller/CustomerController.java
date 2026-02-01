package com.digitalbank.customer.service.controller;


import com.digitalbank.customer.service.dto.*;
import com.digitalbank.customer.service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/api/v1")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerCreatedResponse> createCustomer(@Valid @RequestBody CustomerCreationRequest request) {
        log.info("Customer creation request received -{} ", request);
        CustomerCreatedResponse response = customerService.createCustomer(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/kyc-status")
    public ResponseEntity<String> updateKycStatus(@RequestBody UpdateKycRequest request) {
        log.info("Updating KYC status for userId");
        String response = customerService.updateKycStatus(request.getUserId(), request.getKycStatus());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/details/{userId}")
    public ResponseEntity<CustomerResponse> getCustomerByUserId(@PathVariable String userId) {
        log.info("Fetching customer details by userId -{} ", userId);
        CustomerResponse response = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/details/update")
    public ResponseEntity<CustomerResponse> updateCustomer(@RequestBody UpdateCustomerRequest request) {
        log.info("Updating customer details for userId -{} ", request.getUserId());
        CustomerResponse response = customerService.updateCustomer(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/exists/{emailId}")
    public ResponseEntity<Boolean> getCustomerByEmail(@PathVariable String emailId) {
        log.info("Checking existence of customer by email -{} ", emailId);
        Boolean response = customerService.getCustomerByEmail(emailId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/phone/exists/{phone}")
    public ResponseEntity<Boolean> getCustomerByPhone(@PathVariable String phone) {
        log.info("Checking existence of customer by phone -{} ", phone);
        Boolean response = customerService.getCustomerByPhone(phone);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/user-login")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        log.info("Creating user -{} ", request);
        String response = customerService.createUserLoginDetails(request);
        return ResponseEntity.ok(response);
    }

}
