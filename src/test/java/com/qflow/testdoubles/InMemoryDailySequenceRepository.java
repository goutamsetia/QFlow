package com.qflow.testdoubles;

import com.qflow.repository.DailySequenceRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory test double for DailySequenceRepository.
 */
public class InMemoryDailySequenceRepository implements DailySequenceRepository {

    private final Map<String, AtomicInteger> sequences = new HashMap<>();

    private String key(Long serviceId, LocalDate date) {
        return serviceId + ":" + date.toString();
    }

    @Override
    public int getNextDailySequence(Long serviceId, LocalDate date) {
        return sequences.computeIfAbsent(key(serviceId, date), k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    @Override
    public int getCurrentSequence(Long serviceId, LocalDate date) {
        AtomicInteger ai = sequences.get(key(serviceId, date));
        return ai != null ? ai.get() : 0;
    }

    public void clear() {
        sequences.clear();
    }
}
