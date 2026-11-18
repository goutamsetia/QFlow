package com.qflow.util;

import com.qflow.exception.ValidationException;
import com.qflow.model.Customer;
import com.qflow.model.dto.CustomerRegistrationDTO;
import com.qflow.model.dto.TokenRequestDTO;
import com.qflow.model.enums.CustomerCategory;
import com.qflow.model.enums.Priority;

import java.util.regex.Pattern;

/**
 * Centralized, pure validation utility for Member 1's Customer and Token module.
 * 
 * Enforces business rules, data invariants, and priority determination logic.
 */
public final class InputValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s.\\-']{2,100}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private InputValidator() {
        // Utility class: private constructor prevents instantiation
    }

    /**
     * Validates full customer name (2 to 100 alphabetic characters, spaces, dots, hyphens).
     */
    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("fullName", "Customer full name cannot be null or empty.");
        }
        String trimmed = name.trim();
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("fullName", "Name must be 2-100 characters and contain only alphabetic characters, spaces, dots, or hyphens.");
        }
        return trimmed;
    }

    /**
     * Validates 10-digit domestic mobile number starting with 6, 7, 8, or 9.
     */
    public static String validatePhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("phoneNumber", "Phone number cannot be null or empty.");
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("phoneNumber", "Phone number must be exactly 10 digits starting with 6, 7, 8, or 9.");
        }
        return trimmed;
    }

    /**
     * Validates email if provided. Email is strictly OPTIONAL.
     * Returns null if email is null or blank.
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("email", "Invalid email format: " + trimmed);
        }
        return trimmed;
    }

    /**
     * Validates customer age (acceptable range: 16 to 120).
     */
    public static int validateAge(int age) {
        if (age < 16 || age > 120) {
            throw new ValidationException("age", "Customer age must be between 16 and 120. Provided: " + age);
        }
        return age;
    }

    /**
     * Validates customer category enum.
     */
    public static CustomerCategory validateCategory(CustomerCategory category) {
        if (category == null) {
            throw new ValidationException("category", "Customer category cannot be null.");
        }
        return category;
    }

    /**
     * Validates positive database ID references.
     */
    public static Long validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new ValidationException(fieldName, fieldName + " must be a valid positive identifier.");
        }
        return id;
    }

    /**
     * Comprehensive validation of a CustomerRegistrationDTO.
     */
    public static void validateRegistrationDTO(CustomerRegistrationDTO dto) {
        if (dto == null) {
            throw new ValidationException("CustomerRegistrationDTO cannot be null.");
        }
        validateName(dto.getFullName());
        validatePhoneNumber(dto.getPhoneNumber());
        validateEmail(dto.getEmail());
        validateAge(dto.getAge());
        validateCategory(dto.getCategory());
    }

    /**
     * Priority Decision Rule Engine:
     * 1. Emergency priority requires staff authorization with a non-blank authorization code.
     * 2. Senior citizen priority is granted if customer age >= 60 OR category == SENIOR_CITIZEN.
     * 3. Otherwise, standard NORMAL priority is assigned.
     */
    public static Priority determinePriority(Customer customer, TokenRequestDTO request) {
        if (customer == null) {
            throw new ValidationException("customer", "Customer cannot be null when determining priority.");
        }
        if (request == null) {
            throw new ValidationException("request", "TokenRequestDTO cannot be null when determining priority.");
        }

        // Rule 1: Emergency priority authorization check
        if (request.isEmergencyAuthorized()) {
            String authCode = request.getStaffAuthCode();
            if (authCode == null || authCode.trim().isEmpty()) {
                throw new ValidationException("staffAuthCode", "Emergency priority requires a non-blank staff authorization code.");
            }
            return Priority.EMERGENCY;
        }

        // Rule 2: Age / Demographic Senior Citizen elevation
        if (customer.getAge() >= 60 || customer.getCategory() == CustomerCategory.SENIOR_CITIZEN) {
            return Priority.SENIOR_CITIZEN;
        }

        // Rule 3: Standard normal priority
        return Priority.NORMAL;
    }
}
