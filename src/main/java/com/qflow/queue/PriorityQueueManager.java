package com.qflow.queue;

import com.qflow.model.Token;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PriorityQueueManager implements QueueManager {

    private static final int AVERAGE_SERVICE_MINUTES = 10;

    private static final Comparator<Token> SERVING_ORDER =
            Comparator.comparingInt((Token t) -> t.getPriority().getLevel())
                      .thenComparing(Token::getCreatedAt);

    private final Map<Long, PriorityQueue<Token>> queues = new HashMap<>();
    @Override
    public synchronized void enqueue(Token token) {
        queues.computeIfAbsent(token.getServiceId(), id -> new PriorityQueue<>(SERVING_ORDER))
              .add(token);
    }
        @Override
    public synchronized int getPositionAhead(Token token) {
        PriorityQueue<Token> queue = queues.get(token.getServiceId());
        if (queue == null || !queue.contains(token)) {
            return 0;
        }
        int ahead = 0;
        for (Token other : queue) {
            if (SERVING_ORDER.compare(other, token) < 0) {
                ahead++;
            }
        }
        return ahead;
    }
        @Override
    public synchronized int getEstimatedWaitMinutes(Token token) {
        return getPositionAhead(token) * AVERAGE_SERVICE_MINUTES;
    }

    @Override
    public synchronized boolean cancel(Token token) {
        PriorityQueue<Token> queue = queues.get(token.getServiceId());
        return queue != null && queue.remove(token);
    }
        @Override
    public synchronized int getQueueDepth(Long serviceId) {
        PriorityQueue<Token> queue = queues.get(serviceId);
        return queue == null ? 0 : queue.size();
    }
}