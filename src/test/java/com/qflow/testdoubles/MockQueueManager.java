package com.qflow.testdoubles;

import com.qflow.model.Token;
import com.qflow.model.enums.TokenStatus;
import com.qflow.queue.QueueManager;

import java.util.*;

/**
 * In-memory test double for Member 2's QueueManager.
 * Tracks enqueued tokens, positions, and cancellations.
 */
public class MockQueueManager implements QueueManager {

    private final List<Token> waitingQueue = new ArrayList<>();
    private final Map<Long, Integer> defaultDurations = Map.of(
            1L, 12,
            2L, 8,
            3L, 15,
            4L, 10
    );

    @Override
    public void enqueue(Token token) {
        if (token != null) {
            waitingQueue.add(token);
        }
    }

    @Override
    public int getPositionAhead(Token token) {
        if (token == null || token.getStatus() != TokenStatus.WAITING) {
            return 0;
        }
        int index = waitingQueue.indexOf(token);
        return Math.max(0, index);
    }

    @Override
    public int getEstimatedWaitMinutes(Token token) {
        if (token == null || token.getStatus() != TokenStatus.WAITING) {
            return 0;
        }
        int ahead = getPositionAhead(token);
        int duration = defaultDurations.getOrDefault(token.getServiceId(), 10);
        return ahead * duration;
    }

    @Override
    public boolean cancel(Token token) {
        if (token != null) {
            return waitingQueue.remove(token);
        }
        return false;
    }

    @Override
    public int getQueueDepth(Long serviceId) {
        if (serviceId == null) return waitingQueue.size();
        return (int) waitingQueue.stream()
                .filter(t -> Objects.equals(t.getServiceId(), serviceId))
                .count();
    }

    public List<Token> getWaitingQueue() {
        return Collections.unmodifiableList(waitingQueue);
    }

    public void clear() {
        waitingQueue.clear();
    }
}
