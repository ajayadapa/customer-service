package com.digitalbank.customerservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreatedResponse {
	
	private String userId;
	private String kycStatus;
	private LocalDateTime createdAt;
	

}
