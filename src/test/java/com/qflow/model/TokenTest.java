package com.qflow.model;

import com.qflow.model.enums.Priority;
import com.qflow.model.enums.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Token Domain Model Tests")
class TokenTest {

    @Test
    @DisplayName("Verify business constructor initializes defaults correctly")
    void testBusinessConstructor() {
        Token token = new Token("TKN-ACC-20261118-0001", 10L, 2L, Priority.NORMAL);

        assertNull(token.getId());
        assertEquals("TKN-ACC-20261118-0001", token.getTokenNumber());
        assertEquals(10L, token.getCustomerId());
        assertEquals(2L, token.getServiceId());
        assertNull(token.getCounterId(), "Counter should be null while waiting");
        assertEquals(Priority.NORMAL, token.getPriority());
        assertEquals(TokenStatus.WAITING, token.getStatus(), "Initial status must be WAITING");
        assertNotNull(token.getCreatedAt());
        assertNull(token.getServedAt());
        assertNull(token.getCompletedAt());
    }

    @Test
    @DisplayName("Verify equality and hashcode based on token number")
    void testEqualityAndHashCode() {
        Token t1 = new Token("TKN-ACC-20261118-0001", 10L, 2L, Priority.NORMAL);
        t1.setId(100L);

        Token t2 = new Token("TKN-ACC-20261118-0001", 20L, 2L, Priority.EMERGENCY);
        t2.setId(200L);

        Token t3 = new Token("TKN-REG-20261118-0002", 10L, 1L, Priority.NORMAL);

        assertEquals(t1, t2, "Tokens with identical tokenNumber must be equal");
        assertEquals(t1.hashCode(), t2.hashCode(), "Hash codes must match");
        assertNotEquals(t1, t3, "Tokens with different numbers must not be equal");
    }

    @Test
    @DisplayName("Verify lifecycle timestamp and counter state mutations")
    void testStateTransitions() {
        Token token = new Token("TKN-ACC-20261118-0001", 10L, 2L, Priority.NORMAL);

        // Serving transition
        LocalDateTime servedTime = LocalDateTime.now();
        token.setStatus(TokenStatus.SERVING);
        token.setCounterId(3L);
        token.setServedAt(servedTime);

        assertEquals(TokenStatus.SERVING, token.getStatus());
        assertEquals(3L, token.getCounterId());
        assertEquals(servedTime, token.getServedAt());

        // Completed transition
        LocalDateTime completedTime = servedTime.plusMinutes(8);
        token.setStatus(TokenStatus.COMPLETED);
        token.setCompletedAt(completedTime);

        assertEquals(TokenStatus.COMPLETED, token.getStatus());
        assertEquals(completedTime, token.getCompletedAt());
    }
}
