package com.digitalbank.customer.service.tests;

import com.digitalbank.common.enums.KycStatus;
import com.digitalbank.common.exception.ConflictException;
import com.digitalbank.common.exception.CustomerNotFoundException;
import com.digitalbank.common.model.CustomerEntity;
import com.digitalbank.customer.service.dto.CustomerCreatedResponse;
import com.digitalbank.customer.service.dto.CustomerCreationRequest;
import com.digitalbank.customer.service.dto.CustomerResponse;
import com.digitalbank.customer.service.dto.UpdateCustomerRequest;
import com.digitalbank.customer.service.repository.CustomerRepository;
import com.digitalbank.customer.service.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.digitalbank.common.utils.CustomerConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    /* ---------- CREATE CUSTOMER ---------- */

    @Test
    void createCustomer_whenDuplicateFingerprint_returnsExistingCustomer() {
        CustomerCreationRequest request = new CustomerCreationRequest();
        request.setUserId("user1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setPhone("9999999999");

        CustomerEntity entity = new CustomerEntity();
        entity.setUserId("user1");
        entity.setCustomerIdentity("johndoe9999999999john@test.com");
        entity.setKycStatus(KycStatus.PENDING);

        when(customerRepository.findByUserIdAndEmail("user1", "john@test.com"))
                .thenReturn(Optional.of(entity));

        CustomerCreatedResponse response =
                customerService.createCustomer(request);

        assertEquals(String.valueOf(KycStatus.PENDING), response.getKycStatus());
        verify(customerRepository, never()).saveAndFlush(any());
    }

    @Test
    void createCustomer_whenConflictFingerprint_throwsException() {
        CustomerCreationRequest request = new CustomerCreationRequest();
        request.setUserId("user1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setPhone("9999999999");

        CustomerEntity entity = new CustomerEntity();
        entity.setUserId("user1");
        entity.setCustomerIdentity("different-fingerprint");

        when(customerRepository.findByUserIdAndEmail("user1", "john@test.com"))
                .thenReturn(Optional.of(entity));

        assertThrows(ConflictException.class,
                () -> customerService.createCustomer(request));
    }

    @Test
    void createCustomer_whenNewCustomer_savesSuccessfully() {
        CustomerCreationRequest request = new CustomerCreationRequest();
        request.setUserId("user1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setPhone("9999999999");

        when(customerRepository.findByUserIdAndEmail(any(), any()))
                .thenReturn(Optional.empty());
        when(customerRepository.saveAndFlush(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        CustomerCreatedResponse response =
                customerService.createCustomer(request);

        assertEquals("user1", response.getUserId());
        verify(customerRepository).saveAndFlush(any());
    }

    /* ---------- GET CUSTOMER ---------- */

    @Test
    void getCustomerByUserId_whenFound_returnsCustomer() {
        CustomerEntity entity = new CustomerEntity();
        entity.setUserId("user1");
        entity.setFirstName("John");

        when(customerRepository.findByUserId("user1"))
                .thenReturn(Optional.of(entity));

        CustomerResponse response =
                customerService.getCustomerByUserId("user1");

        assertEquals("user1", response.getUserId());
    }

    @Test
    void getCustomerByUserId_whenNotFound_throwsException() {
        when(customerRepository.findByUserId("user1"))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerByUserId("user1"));
    }

    /* ---------- UPDATE CUSTOMER ---------- */

    @Test
    void updateCustomer_success() {
        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");

        CustomerEntity entity = new CustomerEntity();
        entity.setUserId("user1");

        when(customerRepository.findByUserId("user1"))
                .thenReturn(Optional.of(entity));
        when(customerRepository.save(any()))
                .thenReturn(entity);

        CustomerResponse response =
                customerService.updateCustomer(request);

        assertEquals("user1", response.getUserId());
        verify(customerRepository).save(entity);
    }

    /* ---------- UPDATE KYC ---------- */

    @Test
    void updateKycStatus_whenVerified_returnsSuccess() {
        CustomerEntity entity = new CustomerEntity();
        entity.setUserId("user1");
        entity.setCif("");
        entity.setKycStatus(KycStatus.PENDING);

        when(customerRepository.findByUserId("user1"))
                .thenReturn(Optional.of(entity));

        String result =
                customerService.updateKycStatus("user1", VERIFIED);

        assertEquals(SUCCESS, result);
        assertTrue(entity.getActive());
        assertNotNull(entity.getCif());
        verify(customerRepository).save(entity);
    }

    @Test
    void updateKycStatus_whenInvalid_returnsInvalidKyc() {
        CustomerEntity entity = new CustomerEntity();
        entity.setUserId("user1");

        when(customerRepository.findByUserId("user1"))
                .thenReturn(Optional.of(entity));

        String result =
                customerService.updateKycStatus("user1", "UNKNOWN");

        assertEquals(INVALID_KYC, result);
        verify(customerRepository, never()).save(any());
    }

    /* ---------- EMAIL & PHONE ---------- */

    @Test
    void getCustomerByEmail_returnsTrueWhenExists() {
        when(customerRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(new CustomerEntity()));

        assertTrue(customerService.getCustomerByEmail("test@mail.com"));
    }

    @Test
    void getCustomerByPhone_returnsFalseWhenNotExists() {
        when(customerRepository.findByPhone("9999999999"))
                .thenReturn(Optional.empty());

        assertFalse(customerService.getCustomerByPhone("9999999999"));
    }

    /* ---------- CIF GENERATION ---------- */

    @Test
    void generateCif_returnsValidFormat() {
        String cif = customerService.generateCif();

        assertTrue(cif.startsWith("DB"));
        assertEquals(7, cif.length()); // DB + YY + 3 digits
    }
}

