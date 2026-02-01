package com.digitalbank.customer.service.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreatedResponse {
	
	private String userId;
	private String cif;
	private String kycStatus;
	private LocalDateTime createdAt;

}
