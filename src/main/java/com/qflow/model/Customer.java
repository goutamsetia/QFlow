package com.qflow.model;

import com.qflow.model.enums.CustomerCategory;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a customer, student, or visitor in QFlow.
 * 
 * Phone number acts as the primary natural business key for customer identification
 * and deduplication. Email is optional. Creation timestamp is immutable.
 */
public class Customer {

    private Long id;
    private String customerCode;
    private String fullName;
    private String phoneNumber;
    private String email;
    private int age;
    private CustomerCategory category;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor for mapping/reflection frameworks.
     */
    public Customer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Business constructor for new customer registration (before DB persistence).
     */
    public Customer(String customerCode, String fullName, String phoneNumber, 
                    String email, int age, CustomerCategory category) {
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Full constructor for rehydrating existing records from persistence.
     */
    public Customer(Long id, String customerCode, String fullName, String phoneNumber, 
                    String email, int age, CustomerCategory category, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.category = category;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        this.updatedAt = LocalDateTime.now();
    }

    public CustomerCategory getCategory() {
        return category;
    }

    public void setCategory(CustomerCategory category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Equality is based strictly on the primary business key: phoneNumber.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(phoneNumber, customer.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    /**
     * Safe toString() providing operational identification without leaking full personal details.
     */
    @Override
    public String toString() {
        String maskedPhone = (phoneNumber != null && phoneNumber.length() == 10)
                ? phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7)
                : phoneNumber;
        return "Customer{" +
                "id=" + id +
                ", code='" + customerCode + '\'' +
                ", name='" + fullName + '\'' +
                ", phone='" + maskedPhone + '\'' +
                ", category=" + category +
                '}';
    }
}
