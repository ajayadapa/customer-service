package com.digitalbank.customer.service.service;

import com.digitalbank.common.enums.KycStatus;
import com.digitalbank.common.exception.ConflictException;
import com.digitalbank.common.exception.CustomerNotFoundException;
import com.digitalbank.common.model.CustomerEntity;
import com.digitalbank.common.model.UserEntity;
import com.digitalbank.customer.service.dto.*;
import com.digitalbank.customer.service.repository.CustomerRepository;
import com.digitalbank.customer.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Year;
import java.util.Optional;

import static com.digitalbank.common.utils.CustomerConstants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();

    public CustomerCreatedResponse createCustomer(CustomerCreationRequest request) {

        String customerFingerPrint = request.getFirstName().toLowerCase() + request.getLastName().toLowerCase() + request.getPhone() + request.getEmail().toLowerCase() + request.getIdNumber().toLowerCase();
        //checking for duplicate request
        Optional<CustomerEntity> existingUserId = customerRepository.findByUserIdAndEmailAndIdNumber(request.getUserId(), request.getEmail(), request.getIdNumber());
        if (existingUserId.isPresent()) {
            if (existingUserId.get().getCustomerIdentity().equalsIgnoreCase(customerFingerPrint)) {
                log.warn("Duplicate customer creation request for userId -{} ", request.getUserId());
                throw new ConflictException(DUPLICATE_APPLICATION);
            }
            log.info("Conflict in customer creation request for userId -{} ", request.getUserId());
            throw new ConflictException(EXISTING_CUSTOMER);
        }
        CustomerEntity entity = mapRequestToEntity(request);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setCustomerIdentity(customerFingerPrint);
        entity.setActive(false);
        entity.setUserId(request.getUserId());
        return mapEntityToResponse(customerRepository.saveAndFlush(entity));
    }

    public CustomerResponse getCustomerByUserId(String userId) {
        log.info("Fetching customer details for userId -{} ", userId);
        Optional<CustomerEntity> entity = customerRepository.findByUserId(userId);
        return entity.map(this::createCustomerResponse).orElseThrow(() -> new CustomerNotFoundException(NO_CUSTOMER_ID + userId));
    }

    public CustomerResponse updateCustomer(UpdateCustomerRequest request) {
        log.info("Updating customer details for userId -{} ", request.getUserId());
        CustomerEntity entity = customerRepository.findByUserId(request.getUserId()).orElseThrow(() -> new CustomerNotFoundException(NO_CUSTOMER_ID + request.getUserId()));
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        return createCustomerResponse(customerRepository.save(entity));

    }

    public String updateKycStatus(String userId, String kycStatus) {
        log.info("Updating KYC status for userId -{} to {} ", userId, kycStatus);
        CustomerEntity entity = customerRepository.findByUserId(userId).orElseThrow(() -> new CustomerNotFoundException(NO_CUSTOMER_ID + userId));
        return KycStatus.contains(kycStatus.toUpperCase()) ? updateDB(kycStatus, entity) : INVALID_KYC;
    }

    public Boolean getCustomerByEmail(String emailId) {
        log.info("Checking existence of customer by email -{} ", emailId);
        Optional<CustomerEntity> existingEmailId = customerRepository.findByEmail(emailId);
        return existingEmailId.isPresent() ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean getCustomerByPhone(String phone) {
        log.info("Checking existence of customer by phone -{} ", phone);
        Optional<CustomerEntity> existingPhone = customerRepository.findByPhone(phone);
        return existingPhone.isPresent() ? Boolean.TRUE : Boolean.FALSE;
    }

    public String generateCif() {
        log.info("Generating CIF");
        String bankCode = "DB";
        String year = String.valueOf(Year.now().getValue()).substring(2);
        int randomPart = 100 + random.nextInt(900);
        return bankCode + year + randomPart;
    }

    public String createUserLoginDetails(CreateUserRequest request) {
        log.info("Creating user login details by admin for username -{} ", request.getUsername());
        UserEntity entityUser = new UserEntity();
        entityUser.setUsername(request.getUsername());
        entityUser.setPassword(encoder.encode(request.getPassword()));
        entityUser.setEnabled(true);
        entityUser.setRoles(request.getRoles());
        userRepository.save(entityUser);
        return USER_CREATED_SUCCESS;
    }

    private CustomerEntity mapRequestToEntity(CustomerCreationRequest request) {
        log.info("Mapping customer creation request to entity for userId -{} ", request.getUserId());
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(request.getFirstName());
        customerEntity.setLastName(request.getLastName());
        customerEntity.setPhone(request.getPhone());
        customerEntity.setEmail(request.getEmail());
        customerEntity.setAddress(request.getAddress());
        customerEntity.setIdNumber(request.getIdNumber());
        return customerEntity;
    }

    private CustomerResponse createCustomerResponse(CustomerEntity customerEntity) {
        log.info("Mapping customer entity to response for userId -{} ", customerEntity.getUserId());
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
        response.setCif(StringUtils.isNotBlank(customerEntity.getCif()) ? customerEntity.getCif() : "");
        return response;
    }

    private String updateDB(String kycStatus, CustomerEntity entity) {
        if (kycStatus.equalsIgnoreCase(VERIFIED)) {
            entity.setActive(true);
            if (entity.getCif() == null || entity.getCif().trim().isEmpty()) {
                log.info("Generating CIF and creating user for userId -{} ", entity.getUserId());
                entity.setCif(generateCif());
                createUser(entity);
            }
        }
        entity.setKycStatus(KycStatus.valueOf(kycStatus));
        customerRepository.save(entity);
        return SUCCESS;
    }

    private void createUser(CustomerEntity entity) {
        UserEntity entityUser = new UserEntity();
        log.info("Creating user login details for userId -{} ", entity.getUserId());
        entityUser.setUsername(entity.getUserId());
        entityUser.setPassword(encoder.encode(DEFAULT_PASSWORD));
        entityUser.setEnabled(true);
        entityUser.setRoles(USER_ROLE);
        userRepository.save(entityUser);
    }

    private CustomerCreatedResponse mapEntityToResponse(CustomerEntity entity) {
        CustomerCreatedResponse response = new CustomerCreatedResponse();
        response.setKycStatus(String.valueOf(entity.getKycStatus()));
        response.setCreatedAt(entity.getCreatedAt());
        response.setUserId(entity.getUserId());
        response.setCif(StringUtils.isNotBlank(entity.getCif()) ? entity.getCif() : "");
        return response;
    }

}
