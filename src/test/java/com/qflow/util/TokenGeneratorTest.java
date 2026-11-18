package com.qflow.util;

import com.qflow.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenGenerator Formatting Tests")
class TokenGeneratorTest {

    @Test
    @DisplayName("Verify valid token number formatting")
    void testFormatTokenNumber() {
        LocalDate date = LocalDate.of(2026, 11, 18);
        String token = TokenGenerator.formatTokenNumber("ACC", date, 1);
        assertEquals("TKN-ACC-20261118-0001", token);

        String tokenLargeSeq = TokenGenerator.formatTokenNumber("reg", date, 999);
        assertEquals("TKN-REG-20261118-0999", tokenLargeSeq);
    }

    @Test
    @DisplayName("Verify customer code formatting")
    void testFormatCustomerCode() {
        String code = TokenGenerator.formatCustomerCode(42L, 2026);
        assertEquals("CUST-2026-0042", code);

        String codeLarge = TokenGenerator.formatCustomerCode(10520L, 2026);
        assertEquals("CUST-2026-10520", codeLarge);
    }

    @ParameterizedTest(name = "Invalid service code: {0}")
    @CsvSource({
        "'', Service code cannot be empty",
        "' ', Service code cannot be whitespace",
        "'A', Code must be at least 2 chars",
        "'12', Code cannot be numeric",
        "'TOOLONGCODE', Code must be max 10 chars"
    })
    @DisplayName("Verify invalid service codes throw ValidationException")
    void testInvalidServiceCode(String code, String reason) {
        LocalDate date = LocalDate.of(2026, 11, 18);
        assertThrows(ValidationException.class, () -> TokenGenerator.formatTokenNumber(code, date, 1));
    }

    @Test
    @DisplayName("Null date throws ValidationException")
    void testNullDate() {
        assertThrows(ValidationException.class, () -> TokenGenerator.formatTokenNumber("ACC", null, 1));
    }

    @Test
    @DisplayName("Non-positive daily sequence throws ValidationException")
    void testNonPositiveSequence() {
        LocalDate date = LocalDate.of(2026, 11, 18);
        assertThrows(ValidationException.class, () -> TokenGenerator.formatTokenNumber("ACC", date, 0));
        assertThrows(ValidationException.class, () -> TokenGenerator.formatTokenNumber("ACC", date, -5));
    }

    @Test
    @DisplayName("Invalid customer ID or year throws ValidationException")
    void testInvalidCustomerCodeParams() {
        assertThrows(ValidationException.class, () -> TokenGenerator.formatCustomerCode(null, 2026));
        assertThrows(ValidationException.class, () -> TokenGenerator.formatCustomerCode(0L, 2026));
        assertThrows(ValidationException.class, () -> TokenGenerator.formatCustomerCode(-1L, 2026));
        assertThrows(ValidationException.class, () -> TokenGenerator.formatCustomerCode(1L, 1999));
        assertThrows(ValidationException.class, () -> TokenGenerator.formatCustomerCode(1L, 2101));
    }
}
