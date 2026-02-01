package com.digitalbank.customer.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateUserRequest {

    private String username;
    private String password;
    private Boolean enabled;
    private String roles;
}

