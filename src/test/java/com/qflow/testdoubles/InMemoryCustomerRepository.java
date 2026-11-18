package com.qflow.testdoubles;

import com.qflow.model.Customer;
import com.qflow.repository.CustomerRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory test double for CustomerRepository.
 * Allows Member 1 to run complete unit tests without requiring a live MySQL instance.
 */
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<Long, Customer> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        storage.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return storage.values().stream()
                .filter(c -> Objects.equals(c.getPhoneNumber(), phoneNumber))
                .findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.values().stream()
                .filter(c -> email.equalsIgnoreCase(c.getEmail()))
                .findFirst();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return findByPhoneNumber(phoneNumber).isPresent();
    }

    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
