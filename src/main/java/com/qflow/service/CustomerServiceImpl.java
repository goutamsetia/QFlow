package com.qflow.service;

import com.qflow.exception.ValidationException;
import com.qflow.model.Customer;
import com.qflow.model.dto.CustomerRegistrationDTO;
import com.qflow.repository.CustomerRepository;
import com.qflow.util.InputValidator;
import com.qflow.util.TokenGenerator;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Production implementation of CustomerService (Member 1).
 * 
 * Enforces validation, returning-customer deduplication, and customer code generation.
 * Operates purely through the injected CustomerRepository abstraction without SQL.
 */
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "CustomerRepository cannot be null.");
    }

    @Override
    public Customer registerOrGetCustomer(CustomerRegistrationDTO dto) {
        // 1. Comprehensive input validation
        InputValidator.validateRegistrationDTO(dto);

        String cleanPhone = dto.getPhoneNumber().trim();
        String cleanEmail = InputValidator.validateEmail(dto.getEmail());

        // 2. Customer Deduplication: Check if phone already registered
        Optional<Customer> existingByPhone = customerRepository.findByPhoneNumber(cleanPhone);
        if (existingByPhone.isPresent()) {
            return existingByPhone.get();
        }

        // 3. Optional Email Uniqueness check
        if (cleanEmail != null) {
            Optional<Customer> existingByEmail = customerRepository.findByEmail(cleanEmail);
            if (existingByEmail.isPresent()) {
                throw new ValidationException("email", "Email address is already associated with another customer.");
            }
        }

        // 4. Create and persist new customer entity
        Customer newCustomer = new Customer(
                null,
                dto.getFullName().trim(),
                cleanPhone,
                cleanEmail,
                dto.getAge(),
                dto.getCategory()
        );

        Customer persisted = customerRepository.save(newCustomer);

        // 5. Generate and assign business customer code using allocated primary key
        if (persisted.getCustomerCode() == null || persisted.getCustomerCode().trim().isEmpty()) {
            String code = TokenGenerator.formatCustomerCode(persisted.getId(), LocalDate.now().getYear());
            persisted.setCustomerCode(code);
            persisted = customerRepository.save(persisted);
        }

        return persisted;
    }

    @Override
    public Optional<Customer> findByPhone(String phoneNumber) {
        String cleanPhone = InputValidator.validatePhoneNumber(phoneNumber);
        return customerRepository.findByPhoneNumber(cleanPhone);
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        Long validId = InputValidator.validateId(customerId, "customerId");
        return customerRepository.findById(validId);
    }
}
