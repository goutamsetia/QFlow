package com.qflow.model;

import com.qflow.model.enums.CustomerCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Domain Model Tests")
class CustomerTest {

    @Test
    @DisplayName("Verify business constructor sets fields and defaults correctly")
    void testBusinessConstructor() {
        Customer customer = new Customer("CUST-2026-0001", "Alice Smith", "9876543210", 
                                         "alice@example.com", 21, CustomerCategory.STUDENT);

        assertNull(customer.getId(), "Id should be null before persistence");
        assertEquals("CUST-2026-0001", customer.getCustomerCode());
        assertEquals("Alice Smith", customer.getFullName());
        assertEquals("9876543210", customer.getPhoneNumber());
        assertEquals("alice@example.com", customer.getEmail());
        assertEquals(21, customer.getAge());
        assertEquals(CustomerCategory.STUDENT, customer.getCategory());
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
    }

    @Test
    @DisplayName("Verify optional email can be null")
    void testOptionalEmail() {
        Customer customer = new Customer("CUST-2026-0002", "Bob Jones", "9876543211", 
                                         null, 65, CustomerCategory.SENIOR_CITIZEN);

        assertNull(customer.getEmail(), "Email can be null");
        assertEquals("9876543211", customer.getPhoneNumber());
    }

    @Test
    @DisplayName("Verify equality and hashcode based on phone number")
    void testEqualityAndHashCode() {
        Customer c1 = new Customer("CUST-2026-0001", "Alice", "9876543210", "a@test.com", 20, CustomerCategory.STUDENT);
        c1.setId(1L);

        Customer c2 = new Customer("CUST-2026-0099", "Alice Modified", "9876543210", "other@test.com", 22, CustomerCategory.STAFF);
        c2.setId(2L);

        Customer c3 = new Customer("CUST-2026-0003", "Charlie", "9876543299", "c@test.com", 25, CustomerCategory.GUEST);

        assertEquals(c1, c2, "Customers with matching phone numbers must be considered equal");
        assertEquals(c1.hashCode(), c2.hashCode(), "Hash codes must match for equal customers");
        assertNotEquals(c1, c3, "Customers with distinct phone numbers must not be equal");
    }

    @Test
    @DisplayName("Verify safe toString masks phone number and does not leak email")
    void testSafeToString() {
        Customer customer = new Customer("CUST-2026-0001", "Secret User", "9876543210", "secret@test.com", 30, CustomerCategory.STAFF);
        String str = customer.toString();

        assertTrue(str.contains("987****210"), "Phone number should be masked");
        assertFalse(str.contains("secret@test.com"), "Email should not be exposed in toString");
    }
}
