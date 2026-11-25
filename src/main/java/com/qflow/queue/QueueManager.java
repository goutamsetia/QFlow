package com.qflow.queue;

import com.qflow.model.Token;

/**
 * Integration contract for the Priority Queue Management engine (Owned by Member 2).
 * 
 * Invoked by Member 1's TokenService for queue handoffs, position queries,
 * and token cancellations. Member 2 provides the multithreaded PriorityQueue implementation.
 */
public interface QueueManager {

    /**
     * Enqueues an issued token into its appropriate service queue.
     * 
     * @param token persisted token with status WAITING
     */
    void enqueue(Token token);

    /**
     * Calculates the number of waiting customers ahead of this token in its service queue.
     * 
     * @param token the target token
     * @return 0 if next in line or already serving/completed; otherwise positive integer count
     */
    int getPositionAhead(Token token);

    /**
     * Calculates estimated waiting time in minutes for this token.
     * 
     * @param token the target token
     * @return estimated minutes until counter call
     */
    int getEstimatedWaitMinutes(Token token);

    /**
     * Cancels a waiting token from the active in-memory queue.
     * 
     * @param token the token to cancel
     * @return true if successfully found and removed from waiting queue; false otherwise
     */
    boolean cancel(Token token);

    /**
     * Returns total number of active waiting tokens for a given service.
     */
    int getQueueDepth(Long serviceId);
}
