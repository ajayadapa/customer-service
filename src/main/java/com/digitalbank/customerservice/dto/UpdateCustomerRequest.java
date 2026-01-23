package com.digitalbank.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerRequest {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phone;
}


