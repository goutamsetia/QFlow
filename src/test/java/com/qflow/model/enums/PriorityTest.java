package com.qflow.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Priority Enum Tests")
class PriorityTest {

    @Test
    @DisplayName("Verify priority level hierarchy")
    void testPriorityLevels() {
        assertEquals(1, Priority.EMERGENCY.getLevel());
        assertEquals(2, Priority.SENIOR_CITIZEN.getLevel());
        assertEquals(3, Priority.NORMAL.getLevel());
    }

    @Test
    @DisplayName("Verify relative priority comparison (isHigherPriorityThan)")
    void testIsHigherPriorityThan() {
        assertTrue(Priority.EMERGENCY.isHigherPriorityThan(Priority.SENIOR_CITIZEN));
        assertTrue(Priority.EMERGENCY.isHigherPriorityThan(Priority.NORMAL));
        assertTrue(Priority.SENIOR_CITIZEN.isHigherPriorityThan(Priority.NORMAL));

        assertFalse(Priority.NORMAL.isHigherPriorityThan(Priority.SENIOR_CITIZEN));
        assertFalse(Priority.NORMAL.isHigherPriorityThan(Priority.EMERGENCY));
        assertFalse(Priority.SENIOR_CITIZEN.isHigherPriorityThan(Priority.EMERGENCY));
        assertFalse(Priority.NORMAL.isHigherPriorityThan(Priority.NORMAL));
        assertTrue(Priority.NORMAL.isHigherPriorityThan(null));
    }

    @ParameterizedTest(name = "Level {0} resolves to {1}")
    @CsvSource({
        "1, EMERGENCY",
        "2, SENIOR_CITIZEN",
        "3, NORMAL"
    })
    @DisplayName("Verify resolution from integer level")
    void testFromLevel(int level, Priority expected) {
        assertEquals(expected, Priority.fromLevel(level));
    }

    @Test
    @DisplayName("Verify invalid level throws IllegalArgumentException")
    void testFromLevelInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Priority.fromLevel(0));
        assertThrows(IllegalArgumentException.class, () -> Priority.fromLevel(4));
        assertThrows(IllegalArgumentException.class, () -> Priority.fromLevel(-1));
    }
}
