package com.qflow.repository;

import com.qflow.model.Customer;
import java.util.Optional;

/**
 * Integration repository contract for Customer persistence (Owned by Member 3).
 * 
 * Invoked by Member 1's CustomerService for customer registration and lookups.
 * Member 3 provides the JDBC SQL implementation.
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
