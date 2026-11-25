package com.qflow.repository;

import java.time.LocalDate;

/**
 * Integration repository contract for atomic daily sequence allocation (Owned by Member 3).
 * 
 * Invoked by Member 1's TokenService to obtain the next sequential daily integer
 * for a service department on the specified calendar date.
 */
public interface DailySequenceRepository {

    /**
     * Atomically increments and returns the next daily sequence number for the given service.
     * 
     * @param serviceId service department identifier
     * @param date calendar date of issuance
     * @return strictly positive sequence integer (1, 2, 3...)
     */
    int getNextDailySequence(Long serviceId, LocalDate date);

    /**
     * Queries the current allocated sequence count without incrementing.
     */
    int getCurrentSequence(Long serviceId, LocalDate date);
}
