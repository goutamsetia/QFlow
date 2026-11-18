package com.qflow.service;

import com.qflow.exception.ValidationException;
import com.qflow.model.Customer;
import com.qflow.model.ServiceCategory;
import com.qflow.model.Token;
import com.qflow.model.dto.TokenRequestDTO;
import com.qflow.model.dto.TokenSlipDTO;
import com.qflow.model.dto.TokenStatusDTO;
import com.qflow.model.enums.CustomerCategory;
import com.qflow.model.enums.Priority;
import com.qflow.model.enums.TokenStatus;
import com.qflow.testdoubles.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenService Implementation Tests")
class TokenServiceTest {

    private InMemoryTokenRepository tokenRepository;
    private InMemoryCustomerRepository customerRepository;
    private InMemoryServiceRepository serviceRepository;
    private InMemoryDailySequenceRepository dailySequenceRepository;
    private MockQueueManager queueManager;
    private TokenService tokenService;

    private Customer studentCustomer;
    private Customer seniorCustomer;

    @BeforeEach
    void setUp() {
        tokenRepository = new InMemoryTokenRepository();
        customerRepository = new InMemoryCustomerRepository();
        serviceRepository = new InMemoryServiceRepository();
        dailySequenceRepository = new InMemoryDailySequenceRepository();
        queueManager = new MockQueueManager();

        tokenService = new TokenServiceImpl(
                tokenRepository,
                customerRepository,
                serviceRepository,
                dailySequenceRepository,
                queueManager
        );

        studentCustomer = customerRepository.save(new Customer("CUST-1", "Alice Student", "9876543210", "alice@edu", 20, CustomerCategory.STUDENT));
        seniorCustomer = customerRepository.save(new Customer("CUST-2", "Bob Senior", "9876543211", null, 68, CustomerCategory.SENIOR_CITIZEN));
    }

    @Test
    @DisplayName("Successfully issue normal token via kiosk")
    void testIssueNormalTokenKiosk() {
        // Service 2: Accounts & Fees (ACC, avg 8 min)
        TokenRequestDTO request = TokenRequestDTO.forKiosk(studentCustomer.getId(), 2L);

        TokenSlipDTO slip = tokenService.issueToken(request);

        assertNotNull(slip);
        assertTrue(slip.getTokenNumber().startsWith("TKN-ACC-"));
        assertTrue(slip.getTokenNumber().endsWith("-0001"));
        assertEquals("Alice Student", slip.getCustomerName());
        assertEquals("Accounts & Fee Payments", slip.getServiceName());
        assertEquals(Priority.NORMAL, slip.getPriority());
        assertEquals(0, slip.getQueuePositionAhead(), "First customer has 0 ahead");
        assertEquals(0, slip.getEstimatedWaitMinutes());

        // Verify state in repository and queue
        Token persisted = tokenRepository.findByTokenNumber(slip.getTokenNumber()).orElseThrow();
        assertEquals(TokenStatus.WAITING, persisted.getStatus());
        assertEquals(1, queueManager.getQueueDepth(2L));
    }

    @Test
    @DisplayName("Multiple tokens for same service increment daily sequence and compute wait time")
    void testMultipleTokensSequenceAndPosition() {
        // First token for Accounts
        tokenService.issueToken(TokenRequestDTO.forKiosk(studentCustomer.getId(), 2L));

        // Second token for Accounts
        TokenSlipDTO secondSlip = tokenService.issueToken(TokenRequestDTO.forKiosk(studentCustomer.getId(), 2L));

        assertTrue(secondSlip.getTokenNumber().endsWith("-0002"), "Sequence must increment to 0002");
        assertEquals(1, secondSlip.getQueuePositionAhead(), "Second customer has 1 customer ahead");
        assertEquals(8, secondSlip.getEstimatedWaitMinutes(), "Estimated wait time is 1 * 8 min = 8 min");
    }

    @Test
    @DisplayName("Automatic Senior Citizen priority elevation for age >= 60")
    void testSeniorCitizenAutomaticPriority() {
        TokenRequestDTO request = TokenRequestDTO.forKiosk(seniorCustomer.getId(), 1L); // Registrar
        TokenSlipDTO slip = tokenService.issueToken(request);

        assertEquals(Priority.SENIOR_CITIZEN, slip.getPriority(), "Must automatically assign SENIOR_CITIZEN priority");
    }

    @Test
    @DisplayName("Authorized Emergency priority token issuance")
    void testAuthorizedEmergencyToken() {
        TokenRequestDTO emergencyReq = TokenRequestDTO.forStaffOverride(studentCustomer.getId(), 3L, "EMG-AUTH-999");
        TokenSlipDTO slip = tokenService.issueToken(emergencyReq);

        assertEquals(Priority.EMERGENCY, slip.getPriority(), "Must assign EMERGENCY priority when authorized");
        assertTrue(slip.getTokenNumber().startsWith("TKN-ADM-"));
    }

    @Test
    @DisplayName("Reject emergency request with missing or blank authorization code")
    void testEmergencyMissingAuthCodeRejected() {
        TokenRequestDTO blankAuthReq = new TokenRequestDTO(studentCustomer.getId(), 1L, true, "   ");
        assertThrows(ValidationException.class, () -> tokenService.issueToken(blankAuthReq));
    }

    @Test
    @DisplayName("Reject token request for non-existent customer")
    void testNonExistentCustomerRejected() {
        TokenRequestDTO req = TokenRequestDTO.forKiosk(9999L, 1L);
        ValidationException ex = assertThrows(ValidationException.class, () -> tokenService.issueToken(req));
        assertEquals("customerId", ex.getFieldName());
    }

    @Test
    @DisplayName("Reject token request for non-existent or inactive service")
    void testNonExistentOrInactiveService() {
        TokenRequestDTO badServiceReq = TokenRequestDTO.forKiosk(studentCustomer.getId(), 9999L);
        assertThrows(ValidationException.class, () -> tokenService.issueToken(badServiceReq));

        // Inactive service
        ServiceCategory inactive = new ServiceCategory(5L, "LIB", "Library Desk", 10);
        inactive.setActive(false);
        serviceRepository.addService(inactive);

        TokenRequestDTO inactiveReq = TokenRequestDTO.forKiosk(studentCustomer.getId(), 5L);
        ValidationException ex = assertThrows(ValidationException.class, () -> tokenService.issueToken(inactiveReq));
        assertTrue(ex.getMessage().contains("inactive"));
    }

    @Test
    @DisplayName("Query token status for WAITING token")
    void testQueryStatusWaiting() {
        TokenSlipDTO slip = tokenService.issueToken(TokenRequestDTO.forKiosk(studentCustomer.getId(), 1L));

        TokenStatusDTO status = tokenService.queryStatus(slip.getTokenNumber());

        assertEquals(slip.getTokenNumber(), status.getTokenNumber());
        assertEquals(TokenStatus.WAITING, status.getStatus());
        assertNull(status.getCounterNumber());
        assertNull(status.getOperatorName());
    }

    @Test
    @DisplayName("Query token status for SERVING token")
    void testQueryStatusServing() {
        TokenSlipDTO slip = tokenService.issueToken(TokenRequestDTO.forKiosk(studentCustomer.getId(), 1L));

        // Simulate operator calling customer to Counter 4
        Token token = tokenRepository.findByTokenNumber(slip.getTokenNumber()).orElseThrow();
        token.setStatus(TokenStatus.SERVING);
        token.setCounterId(4L);
        tokenRepository.save(token);

        TokenStatusDTO status = tokenService.queryStatus(slip.getTokenNumber());

        assertEquals(TokenStatus.SERVING, status.getStatus());
        assertEquals(4, status.getCounterNumber());
        assertNotNull(status.getOperatorName());
        assertEquals(0, status.getQueuePositionAhead());
    }

    @Test
    @DisplayName("Cancel token successfully when in WAITING status")
    void testCancelTokenSuccess() {
        TokenSlipDTO slip = tokenService.issueToken(TokenRequestDTO.forKiosk(studentCustomer.getId(), 1L));

        boolean cancelled = tokenService.cancelToken(slip.getTokenNumber());
        assertTrue(cancelled, "Cancellation of waiting token must succeed");

        TokenStatusDTO status = tokenService.queryStatus(slip.getTokenNumber());
        assertEquals(TokenStatus.CANCELLED, status.getStatus());
        assertEquals(0, queueManager.getQueueDepth(1L), "Token must be removed from queue");
    }

    @Test
    @DisplayName("Cannot cancel token if already SERVING or COMPLETED")
    void testCannotCancelServingToken() {
        TokenSlipDTO slip = tokenService.issueToken(TokenRequestDTO.forKiosk(studentCustomer.getId(), 1L));

        Token token = tokenRepository.findByTokenNumber(slip.getTokenNumber()).orElseThrow();
        token.setStatus(TokenStatus.SERVING);
        tokenRepository.save(token);

        boolean cancelled = tokenService.cancelToken(slip.getTokenNumber());
        assertFalse(cancelled, "Cannot cancel token that is already SERVING");
    }

    @Test
    @DisplayName("Query or cancel non-existent token throws ValidationException")
    void testNonExistentTokenQueries() {
        assertThrows(ValidationException.class, () -> tokenService.queryStatus("TKN-FAKE-0001"));
        assertThrows(ValidationException.class, () -> tokenService.cancelToken("TKN-FAKE-0001"));
        assertThrows(ValidationException.class, () -> tokenService.queryStatus(null));
    }
}
