package com.qflow.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerCategory Enum Tests")
class CustomerCategoryTest {

    @Test
    @DisplayName("Verify expected categories exist and have display names")
    void testCategories() {
        assertEquals(4, CustomerCategory.values().length);
        assertNotNull(CustomerCategory.STUDENT.getDisplayName());
        assertNotNull(CustomerCategory.STAFF.getDisplayName());
        assertNotNull(CustomerCategory.SENIOR_CITIZEN.getDisplayName());
        assertNotNull(CustomerCategory.GUEST.getDisplayName());
    }
}
