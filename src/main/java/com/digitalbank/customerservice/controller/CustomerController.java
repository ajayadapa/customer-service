package com.digitalbank.customerservice.controller;


import com.digitalbank.customerservice.dto.CustomerCreatedResponse;
import com.digitalbank.customerservice.dto.CustomerCreationRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.dto.UpdateCustomerRequest;
import com.digitalbank.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerCreatedResponse> createCustomer(@Valid @RequestBody CustomerCreationRequest request) {
        CustomerCreatedResponse response = customerService.createCustomer(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<CustomerResponse> getCustomerByUserId(@PathVariable String userId) {
        CustomerResponse response = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update/{userId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable String userId, @RequestBody UpdateCustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/kyc-status/{userId}/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateKycStatus(@PathVariable String userId, @RequestParam String kycStatus) {
        String response = customerService.updateKycStatus(userId, kycStatus);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/exists/{emailId}")
    public ResponseEntity<Boolean> getCustomerByEmail(@PathVariable String emailId) {
        Boolean response = customerService.getCustomerByEmail(emailId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/phone/exists/{phone}")
    public ResponseEntity<Boolean> getCustomerByPhone(@PathVariable String phone) {
        Boolean response = customerService.getCustomerByPhone(phone);
        return ResponseEntity.ok(response);
    }

}
