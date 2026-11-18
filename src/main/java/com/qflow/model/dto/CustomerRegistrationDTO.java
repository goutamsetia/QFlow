package com.qflow.model.dto;

import com.qflow.model.enums.CustomerCategory;

/**
 * Data Transfer Object for capturing customer registration input from the console kiosk.
 */
public class CustomerRegistrationDTO {

    private final String fullName;
    private final String phoneNumber;
    private final String email;
    private final int age;
    private final CustomerCategory category;

    public CustomerRegistrationDTO(String fullName, String phoneNumber, String email, 
                                   int age, CustomerCategory category) {
        this.fullName = fullName != null ? fullName.trim() : null;
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        this.email = (email != null && !email.trim().isEmpty()) ? email.trim() : null;
        this.age = age;
        this.category = category;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public CustomerCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "CustomerRegistrationDTO{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", category=" + category +
                '}';
    }
}
