package com.qflow.util;

import com.qflow.exception.ValidationException;
import com.qflow.model.Customer;
import com.qflow.model.dto.CustomerRegistrationDTO;
import com.qflow.model.dto.TokenRequestDTO;
import com.qflow.model.enums.CustomerCategory;
import com.qflow.model.enums.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InputValidator Comprehensive Tests")
class InputValidatorTest {

    @ParameterizedTest(name = "Valid name: {0}")
    @ValueSource(strings = {
        "John Doe",
        "A. B. Smith",
        "Jean-Luc Picard",
        "O'Connor",
        "Prof. Robert Davis"
    })
    @DisplayName("Verify valid full names")
    void testValidNames(String name) {
        assertDoesNotThrow(() -> InputValidator.validateName(name));
        assertEquals(name.trim(), InputValidator.validateName(name));
    }

    @ParameterizedTest(name = "Invalid name: {0}")
    @ValueSource(strings = {
        "",
        "   ",
        "A",
        "John123",
        "Jane@Doe",
        "This name is way too long and exceeds one hundred characters by a wide margin which should be completely rejected by our validator"
    })
    @DisplayName("Verify invalid names throw ValidationException")
    void testInvalidNames(String name) {
        assertThrows(ValidationException.class, () -> InputValidator.validateName(name));
    }

    @Test
    @DisplayName("Null name throws ValidationException")
    void testNullName() {
        assertThrows(ValidationException.class, () -> InputValidator.validateName(null));
    }

    @ParameterizedTest(name = "Valid phone: {0}")
    @ValueSource(strings = {
        "9876543210",
        "8765432109",
        "7654321098",
        "6543210987"
    })
    @DisplayName("Verify valid 10-digit phone numbers")
    void testValidPhones(String phone) {
        assertDoesNotThrow(() -> InputValidator.validatePhoneNumber(phone));
    }

    @ParameterizedTest(name = "Invalid phone: {0}")
    @ValueSource(strings = {
        "1234567890", // Starts with 1
        "5556667777", // Starts with 5
        "987654321",  // 9 digits
        "98765432100",// 11 digits
        "98765abcde", // Letters
        "",
        "          "
    })
    @DisplayName("Verify invalid phone numbers throw ValidationException")
    void testInvalidPhones(String phone) {
        assertThrows(ValidationException.class, () -> InputValidator.validatePhoneNumber(phone));
    }

    @Test
    @DisplayName("Valid optional email passes, null or empty returns null")
    void testEmailValidation() {
        assertNull(InputValidator.validateEmail(null));
        assertNull(InputValidator.validateEmail(""));
        assertNull(InputValidator.validateEmail("   "));

        assertEquals("test@example.com", InputValidator.validateEmail("test@example.com"));
        assertEquals("user.name+tag@sub.domain.edu", InputValidator.validateEmail("  user.name+tag@sub.domain.edu  "));

        assertThrows(ValidationException.class, () -> InputValidator.validateEmail("invalid-email"));
        assertThrows(ValidationException.class, () -> InputValidator.validateEmail("missing@domain"));
    }

    @ParameterizedTest(name = "Valid age: {0}")
    @ValueSource(ints = {16, 17, 25, 60, 85, 120})
    @DisplayName("Verify acceptable age boundaries")
    void testValidAge(int age) {
        assertEquals(age, InputValidator.validateAge(age));
    }

    @ParameterizedTest(name = "Invalid age: {0}")
    @ValueSource(ints = {-1, 0, 15, 121, 150})
    @DisplayName("Verify age out of bounds throws ValidationException")
    void testInvalidAge(int age) {
        assertThrows(ValidationException.class, () -> InputValidator.validateAge(age));
    }

    @Test
    @DisplayName("Priority Rule 1: Emergency priority requires staff authorization code")
    void testEmergencyPriorityRules() {
        Customer student = new Customer("CUST-1", "Alice", "9876543210", null, 20, CustomerCategory.STUDENT);

        // Valid staff override
        TokenRequestDTO authorizedReq = TokenRequestDTO.forStaffOverride(1L, 2L, "MED-EMG-01");
        assertEquals(Priority.EMERGENCY, InputValidator.determinePriority(student, authorizedReq));

        // Missing or blank auth code throws ValidationException
        TokenRequestDTO blankAuthReq = new TokenRequestDTO(1L, 2L, true, "   ");
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> InputValidator.determinePriority(student, blankAuthReq));
        assertEquals("staffAuthCode", ex.getFieldName());

        TokenRequestDTO nullAuthReq = new TokenRequestDTO(1L, 2L, true, null);
        assertThrows(ValidationException.class, 
            () -> InputValidator.determinePriority(student, nullAuthReq));
    }

    @Test
    @DisplayName("Priority Rule 2: Senior Citizen priority via age >= 60 or SENIOR_CITIZEN category")
    void testSeniorPriorityRules() {
        TokenRequestDTO kioskReq = TokenRequestDTO.forKiosk(1L, 2L);

        // Age >= 60
        Customer seniorByAge = new Customer("CUST-1", "Senior Age", "9876543210", null, 65, CustomerCategory.GUEST);
        assertEquals(Priority.SENIOR_CITIZEN, InputValidator.determinePriority(seniorByAge, kioskReq));

        // Category == SENIOR_CITIZEN (even if age < 60)
        Customer seniorByCategory = new Customer("CUST-2", "Senior Cat", "9876543211", null, 59, CustomerCategory.SENIOR_CITIZEN);
        assertEquals(Priority.SENIOR_CITIZEN, InputValidator.determinePriority(seniorByCategory, kioskReq));
    }

    @Test
    @DisplayName("Priority Rule 3: Normal priority for general students and staff")
    void testNormalPriorityRule() {
        TokenRequestDTO kioskReq = TokenRequestDTO.forKiosk(1L, 2L);

        Customer student = new Customer("CUST-1", "Alice Student", "9876543210", null, 20, CustomerCategory.STUDENT);
        assertEquals(Priority.NORMAL, InputValidator.determinePriority(student, kioskReq));

        Customer staff = new Customer("CUST-2", "Bob Staff", "9876543211", null, 45, CustomerCategory.STAFF);
        assertEquals(Priority.NORMAL, InputValidator.determinePriority(staff, kioskReq));
    }

    @Test
    @DisplayName("Comprehensive DTO validation catches invalid fields")
    void testValidateRegistrationDTO() {
        CustomerRegistrationDTO valid = new CustomerRegistrationDTO("Alice", "9876543210", "alice@test.com", 20, CustomerCategory.STUDENT);
        assertDoesNotThrow(() -> InputValidator.validateRegistrationDTO(valid));

        CustomerRegistrationDTO invalidPhone = new CustomerRegistrationDTO("Alice", "123", null, 20, CustomerCategory.STUDENT);
        assertThrows(ValidationException.class, () -> InputValidator.validateRegistrationDTO(invalidPhone));
    }
}
