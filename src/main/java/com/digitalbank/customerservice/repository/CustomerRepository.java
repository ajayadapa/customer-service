package com.digitalbank.customerservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalbank.customerservice.models.CustomerEntity;


public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

	Optional<CustomerEntity> findByUserIdAndEmail(String userId,String email);
	Optional<CustomerEntity> findByEmail(String email);
	Optional<CustomerEntity> findByUserId(String userId);
	Optional<CustomerEntity> findByPhone(String phone);


}
