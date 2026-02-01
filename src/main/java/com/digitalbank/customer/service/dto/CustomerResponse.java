package com.digitalbank.customer.service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String userId;
    private Boolean active;
    private String kycStatus;
    private String cif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
