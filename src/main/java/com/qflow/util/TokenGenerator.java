package com.qflow.util;

import com.qflow.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Deterministic business code and token number formatting utility.
 * 
 * Important Boundary Note:
 * This utility ONLY formats strings using already-allocated sequence numbers.
 * Persistent sequence allocation is the responsibility of Member 3 (DailySequenceRepository).
 */
public final class TokenGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    private TokenGenerator() {
        // Utility class: private constructor prevents instantiation
    }

    /**
     * Formats a business customer code.
     * 
     * Format: CUST-<YEAR>-<ZERO_PADDED_ID> (e.g., CUST-2026-0001)
     * 
     * @param customerId positive database customer identifier
     * @param year the 4-digit calendar year
     * @return formatted customer code
     */
    public static String formatCustomerCode(Long customerId, int year) {
        if (customerId == null || customerId <= 0) {
            throw new ValidationException("customerId", "Customer ID must be a positive number for code formatting.");
        }
        if (year < 2000 || year > 2100) {
            throw new ValidationException("year", "Year must be a valid 4-digit year between 2000 and 2100.");
        }
        return String.format("CUST-%d-%04d", year, customerId);
    }

    /**
     * Formats an immutable, human-readable token number.
     * 
     * Format: TKN-<SERVICE_CODE>-<YYYYMMDD>-<ZERO_PADDED_DAILY_SEQUENCE>
     * Example: TKN-ACC-20261118-0001
     * 
     * @param serviceCode 2 to 5 character uppercase service abbreviation (e.g. "REG", "ACC")
     * @param date calendar date of issuance
     * @param dailySequence positive daily sequence integer allocated by database
     * @return formatted token string
     */
    public static String formatTokenNumber(String serviceCode, LocalDate date, int dailySequence) {
        if (serviceCode == null || serviceCode.trim().isEmpty()) {
            throw new ValidationException("serviceCode", "Service code cannot be null or empty.");
        }
        String cleanCode = serviceCode.trim().toUpperCase();
        if (!cleanCode.matches("^[A-Z]{2,10}$")) {
            throw new ValidationException("serviceCode", "Service code must be 2-10 uppercase alphabetic characters.");
        }
        if (date == null) {
            throw new ValidationException("date", "Issuance date cannot be null.");
        }
        if (dailySequence <= 0) {
            throw new ValidationException("dailySequence", "Daily sequence must be a strictly positive integer (> 0).");
        }

        String dateStr = date.format(DATE_FORMATTER);
        return String.format("TKN-%s-%s-%04d", cleanCode, dateStr, dailySequence);
    }
}
