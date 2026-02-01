package com.digitalbank.customer.service.repository;


import com.digitalbank.common.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
