package com.digitalbank.customerservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="customers",
uniqueConstraints = {
		@UniqueConstraint ( name = "unique_customer_email_constraint", columnNames = { "email" }),
		@UniqueConstraint(name = "Unique_customer_userId_constriant", columnNames = { "user_Id" })
})
public class CustomerEntity {
    
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="first_name", nullable =false)
	private String firstName;
	@Column(name="last_name", nullable =false)
	private String lastName;
	@Column(name="email", nullable =false)
	private String email;
	@Column(name="phone", nullable =false)
	private String phone;
	@Column(name="address", nullable =false)
	private String address;
	@Column(name="user_Id", nullable =false)
	private String userId;
	@Column(name="active", nullable =false)
	private Boolean active;
	@Enumerated(EnumType.STRING)
	@Column(name="kyc_status")
	private KycStatus kycStatus;
	@Column(name="created_date", nullable =false)
	private LocalDateTime createdAt;
	@Column(name="updated_date", nullable =false)
	private LocalDateTime updatedAt;
	@Column(name ="customer_identity")
	private String customerIdentity;

	//inserting before new entity
	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
    //before updating existing entity
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
