package com.qflow.service;

import com.qflow.model.Customer;
import com.qflow.model.dto.CustomerRegistrationDTO;
import java.util.Optional;

/**
 * Service contract for Customer registration, deduplication, and lookup (Owned by Member 1).
 */
public interface CustomerService {

    /**
     * Registers a new customer or retrieves the existing profile if phone number matches.
     * 
     * Performs comprehensive field validation before querying or persisting.
     * 
     * @param dto registration payload
     * @return persisted or existing Customer entity
     * @throws com.qflow.exception.ValidationException if input validation fails
     */
    Customer registerOrGetCustomer(CustomerRegistrationDTO dto);

    /**
     * Finds a customer by their unique 10-digit phone number.
     * 
     * @param phoneNumber 10-digit mobile number
     * @return Optional containing customer if found, empty otherwise
     */
    Optional<Customer> findByPhone(String phoneNumber);

    /**
     * Finds a customer by their primary database ID.
     * 
     * @param customerId surrogate database key
     * @return Optional containing customer if found, empty otherwise
     */
    Optional<Customer> findById(Long customerId);
}
