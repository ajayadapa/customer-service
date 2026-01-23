package com.digitalbank.customerservice.service;

import com.digitalbank.customerservice.dto.CustomerCreatedResponse;
import com.digitalbank.customerservice.dto.CustomerCreationRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.dto.UpdateCustomerRequest;
import com.digitalbank.customerservice.models.CustomerEntity;
import com.digitalbank.customerservice.models.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;
import com.digitalbank.dto.exception.customer.ConflictException;
import com.digitalbank.dto.exception.customer.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.digitalbank.customerservice.utils.Constants.*;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public CustomerCreatedResponse createCustomer(CustomerCreationRequest request) {

        String customerFingerPrint = request.getFirstName().toLowerCase() + request.getLastName().toLowerCase() + request.getPhone() + request.getEmail().toLowerCase();
        //checking for duplicate request
        Optional<CustomerEntity> existingUserId = customerRepository.findByUserIdAndEmail(request.getUserId(), request.getEmail());
        if (existingUserId.isPresent()) {
            if (existingUserId.get().getCustomerIdentity().equalsIgnoreCase(customerFingerPrint)) {
                return mapEntityToResponse(existingUserId.get());
            }
            throw new ConflictException(EXISTING_CUSTOMER);
        }

        CustomerEntity entity = mapRequestToEntity(request);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setCustomerIdentity(customerFingerPrint);
        entity.setActive(false);
        entity.setUserId(request.getUserId());
        return mapEntityToResponse(customerRepository.saveAndFlush(entity));
    }

    private CustomerCreatedResponse mapEntityToResponse(CustomerEntity entity) {
        CustomerCreatedResponse response = new CustomerCreatedResponse();
        response.setKycStatus(String.valueOf(entity.getKycStatus()));
        response.setCreatedAt(entity.getCreatedAt());
        response.setUserId(entity.getUserId());
        return response;
    }

    public CustomerResponse getCustomerByUserId(String userId) {

        Optional<CustomerEntity> entity = customerRepository.findByUserId(userId);
        return entity.map(this::createCustomerResponse).orElseThrow(() -> new CustomerNotFoundException(NO_CUSTOMER_ID + userId));

    }

    private CustomerResponse createCustomerResponse(CustomerEntity customerEntity) {

        CustomerResponse response = new CustomerResponse();
        response.setFirstName(customerEntity.getFirstName());
        response.setLastName(customerEntity.getLastName());
        response.setEmail(customerEntity.getEmail());
        response.setPhone(customerEntity.getPhone());
        response.setAddress(customerEntity.getAddress());
        response.setUserId(customerEntity.getUserId());
        response.setActive(customerEntity.getActive());
        response.setKycStatus(String.valueOf(customerEntity.getKycStatus()));
        response.setUpdatedAt(customerEntity.getUpdatedAt());
        response.setCreatedAt(customerEntity.getCreatedAt());
        return response;
    }

    public CustomerResponse updateCustomer(String userId, UpdateCustomerRequest request) {
        CustomerEntity entity = customerRepository.findByUserId(userId).orElseThrow(() -> new CustomerNotFoundException(NO_CUSTOMER_ID + userId));
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        return createCustomerResponse(customerRepository.save(entity));

    }

    private CustomerEntity mapRequestToEntity(CustomerCreationRequest entity) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(entity.getFirstName());
        customerEntity.setLastName(entity.getLastName());
        customerEntity.setPhone(entity.getPhone());
        customerEntity.setEmail(entity.getEmail());
        customerEntity.setAddress(entity.getAddress());
        return customerEntity;
    }

    public String updateKycStatus(String userId, String kycStatus) {
        CustomerEntity entity = customerRepository.findByUserId(userId).orElseThrow(() -> new CustomerNotFoundException(NO_CUSTOMER_ID + userId));
        return KycStatus.contains(kycStatus.toUpperCase()) ? updateDB(kycStatus, entity) : INVALID_KYC;
    }

    private String updateDB(String kycStatus, CustomerEntity entity) {
        if (kycStatus.equalsIgnoreCase(VERIFIED)) {
            entity.setActive(true);
        }
        entity.setKycStatus(KycStatus.valueOf(kycStatus));
        customerRepository.save(entity);
        return SUCCESS;
    }

    public Boolean getCustomerByEmail(String emailId) {
        Optional<CustomerEntity> existingEmailId = customerRepository.findByEmail(emailId);
        return existingEmailId.isPresent() ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean getCustomerByPhone(String phone) {
        Optional<CustomerEntity> existingPhone = customerRepository.findByPhone(phone);
        return existingPhone.isPresent() ? Boolean.TRUE : Boolean.FALSE;
    }
}
