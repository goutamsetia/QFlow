package com.qflow.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenStatus Enum Tests")
class TokenStatusTest {

    @Test
    @DisplayName("Verify terminal status identification")
    void testIsTerminal() {
        // Terminal statuses
        assertTrue(TokenStatus.COMPLETED.isTerminal(), "COMPLETED must be terminal");
        assertTrue(TokenStatus.SKIPPED.isTerminal(), "SKIPPED must be terminal");
        assertTrue(TokenStatus.CANCELLED.isTerminal(), "CANCELLED must be terminal");

        // Non-terminal statuses
        assertFalse(TokenStatus.WAITING.isTerminal(), "WAITING must be non-terminal");
        assertFalse(TokenStatus.SERVING.isTerminal(), "SERVING must be non-terminal");
        assertFalse(TokenStatus.INTERRUPTED.isTerminal(), "INTERRUPTED must be non-terminal (pending admin triage)");
    }

    @Test
    @DisplayName("Verify status descriptions are present")
    void testDescriptions() {
        for (TokenStatus status : TokenStatus.values()) {
            assertNotNull(status.getDescription());
            assertFalse(status.getDescription().trim().isEmpty());
        }
    }
}
