package com.qflow.service;

import com.qflow.exception.ValidationException;
import com.qflow.model.Customer;
import com.qflow.model.dto.CustomerRegistrationDTO;
import com.qflow.model.enums.CustomerCategory;
import com.qflow.testdoubles.InMemoryCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerService Implementation Tests")
class CustomerServiceTest {

    private InMemoryCustomerRepository customerRepository;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    @DisplayName("Successfully register a new customer with valid mandatory and optional fields")
    void testRegisterNewCustomerSuccess() {
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO(
                "Alice Smith", "9876543210", "alice@test.edu", 21, CustomerCategory.STUDENT
        );

        Customer registered = customerService.registerOrGetCustomer(dto);

        assertNotNull(registered.getId());
        assertEquals("Alice Smith", registered.getFullName());
        assertEquals("9876543210", registered.getPhoneNumber());
        assertEquals("alice@test.edu", registered.getEmail());
        assertEquals(21, registered.getAge());
        assertEquals(CustomerCategory.STUDENT, registered.getCategory());
        assertNotNull(registered.getCustomerCode(), "Customer code should be automatically generated");
        assertTrue(registered.getCustomerCode().startsWith("CUST-"));
    }

    @Test
    @DisplayName("Successfully register a customer without email (email is optional)")
    void testRegisterCustomerWithoutEmail() {
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO(
                "Bob Jones", "9876543211", null, 68, CustomerCategory.SENIOR_CITIZEN
        );

        Customer registered = customerService.registerOrGetCustomer(dto);

        assertNotNull(registered.getId());
        assertNull(registered.getEmail());
        assertEquals("9876543211", registered.getPhoneNumber());
    }

    @Test
    @DisplayName("Deduplication: Registering with an existing phone number reuses existing profile")
    void testCustomerDeduplicationByPhone() {
        CustomerRegistrationDTO first = new CustomerRegistrationDTO(
                "Alice Smith", "9876543210", "alice@test.edu", 21, CustomerCategory.STUDENT
        );
        Customer original = customerService.registerOrGetCustomer(first);

        CustomerRegistrationDTO second = new CustomerRegistrationDTO(
                "Alice S.", "9876543210", "different@test.edu", 22, CustomerCategory.STUDENT
        );
        Customer reused = customerService.registerOrGetCustomer(second);

        assertEquals(original.getId(), reused.getId(), "Must reuse existing customer ID");
        assertEquals(original.getCustomerCode(), reused.getCustomerCode());
    }

    @Test
    @DisplayName("Reject registration if email already belongs to another customer")
    void testDuplicateEmailRejection() {
        CustomerRegistrationDTO first = new CustomerRegistrationDTO(
                "Alice", "9876543210", "shared@test.edu", 20, CustomerCategory.STUDENT
        );
        customerService.registerOrGetCustomer(first);

        CustomerRegistrationDTO second = new CustomerRegistrationDTO(
                "Bob", "9876543211", "shared@test.edu", 25, CustomerCategory.STAFF
        );

        ValidationException ex = assertThrows(ValidationException.class, 
            () -> customerService.registerOrGetCustomer(second));
        assertEquals("email", ex.getFieldName());
    }

    @Test
    @DisplayName("Reject registration with invalid phone or age")
    void testInvalidRegistrationFields() {
        CustomerRegistrationDTO badPhone = new CustomerRegistrationDTO("Alice", "12345", null, 20, CustomerCategory.STUDENT);
        assertThrows(ValidationException.class, () -> customerService.registerOrGetCustomer(badPhone));

        CustomerRegistrationDTO badAge = new CustomerRegistrationDTO("Alice", "9876543210", null, 14, CustomerCategory.STUDENT);
        assertThrows(ValidationException.class, () -> customerService.registerOrGetCustomer(badAge));
    }

    @Test
    @DisplayName("Find customer by phone number")
    void testFindByPhone() {
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Charlie", "9876543299", null, 30, CustomerCategory.GUEST);
        customerService.registerOrGetCustomer(dto);

        Optional<Customer> found = customerService.findByPhone("9876543299");
        assertTrue(found.isPresent());
        assertEquals("Charlie", found.get().getFullName());

        Optional<Customer> notFound = customerService.findByPhone("9876543200");
        assertTrue(notFound.isEmpty());
    }

    @Test
    @DisplayName("Find customer by ID")
    void testFindById() {
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("David", "9876543288", null, 24, CustomerCategory.STUDENT);
        Customer saved = customerService.registerOrGetCustomer(dto);

        Optional<Customer> found = customerService.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("David", found.get().getFullName());

        assertThrows(ValidationException.class, () -> customerService.findById(-1L));
        assertThrows(ValidationException.class, () -> customerService.findById(null));
    }
}
