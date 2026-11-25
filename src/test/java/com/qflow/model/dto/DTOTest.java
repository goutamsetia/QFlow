package com.qflow.model.dto;

import com.qflow.model.enums.CustomerCategory;
import com.qflow.model.enums.Priority;
import com.qflow.model.enums.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Data Transfer Objects (DTO) Tests")
class DTOTest {

    @Test
    @DisplayName("CustomerRegistrationDTO trims input and handles optional email correctly")
    void testCustomerRegistrationDTO() {
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO(
                "  John Doe  ", " 9876543210 ", "  john@doe.com  ", 25, CustomerCategory.STUDENT
        );

        assertEquals("John Doe", dto.getFullName());
        assertEquals("9876543210", dto.getPhoneNumber());
        assertEquals("john@doe.com", dto.getEmail());
        assertEquals(25, dto.getAge());
        assertEquals(CustomerCategory.STUDENT, dto.getCategory());

        // Empty email becomes null
        CustomerRegistrationDTO dtoNoEmail = new CustomerRegistrationDTO("Jane", "9876543211", "   ", 22, CustomerCategory.STAFF);
        assertNull(dtoNoEmail.getEmail());
    }

    @Test
    @DisplayName("TokenRequestDTO kiosk factory never authorizes emergency priority")
    void testTokenRequestDTOKiosk() {
        TokenRequestDTO kioskReq = TokenRequestDTO.forKiosk(1L, 2L);

        assertEquals(1L, kioskReq.getCustomerId());
        assertEquals(2L, kioskReq.getServiceId());
        assertFalse(kioskReq.isEmergencyAuthorized(), "Kiosk requests must never be authorized as emergency");
        assertNull(kioskReq.getStaffAuthCode());
    }

    @Test
    @DisplayName("TokenRequestDTO staff override enables emergency with auth code")
    void testTokenRequestDTOStaffOverride() {
        TokenRequestDTO staffReq = TokenRequestDTO.forStaffOverride(1L, 2L, "MED-EMG-101");

        assertTrue(staffReq.isEmergencyAuthorized());
        assertEquals("MED-EMG-101", staffReq.getStaffAuthCode());
    }

    @Test
    @DisplayName("TokenSlipDTO formats ASCII slip with all relevant details")
    void testTokenSlipDTO() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 18, 10, 30, 0);
        TokenSlipDTO slip = new TokenSlipDTO("TKN-ACC-20261118-0001", "Alice Smith", "Accounts", 
                                             Priority.NORMAL, 3, 24, now);

        assertEquals("TKN-ACC-20261118-0001", slip.getTokenNumber());
        assertEquals("Alice Smith", slip.getCustomerName());
        assertEquals(3, slip.getQueuePositionAhead());
        assertEquals(24, slip.getEstimatedWaitMinutes());

        String formatted = slip.toFormattedSlip();
        assertTrue(formatted.contains("TKN-ACC-20261118-0001"));
        assertTrue(formatted.contains("Alice Smith"));
        assertTrue(formatted.contains("Accounts"));
        assertTrue(formatted.contains("Normal"));
        assertTrue(formatted.contains("3 customer(s)"));
        assertTrue(formatted.contains("~24 minutes"));
    }

    @Test
    @DisplayName("TokenStatusDTO preserves status, counter, and queue metrics")
    void testTokenStatusDTO() {
        TokenStatusDTO status = new TokenStatusDTO("TKN-REG-20261118-0005", TokenStatus.SERVING, 
                                                   2, "Mr. Verma", 0, 0);

        assertEquals("TKN-REG-20261118-0005", status.getTokenNumber());
        assertEquals(TokenStatus.SERVING, status.getStatus());
        assertEquals(2, status.getCounterNumber());
        assertEquals("Mr. Verma", status.getOperatorName());
        assertEquals(0, status.getQueuePositionAhead());
        assertEquals(0, status.getEstimatedWaitMinutes());
    }
}
