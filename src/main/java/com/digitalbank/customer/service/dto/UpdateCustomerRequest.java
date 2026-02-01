package com.digitalbank.customer.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phone;
    @NotBlank
    private String userId;
}


