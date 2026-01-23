package com.digitalbank.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String userId;
    private Boolean active;
    private String kycStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
