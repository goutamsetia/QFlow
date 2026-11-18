package com.qflow.model.enums;

/**
 * Represents the lifecycle stages of a customer service token in QFlow.
 * 
 * Flow:
 * WAITING -> SERVING -> COMPLETED / SKIPPED
 * WAITING -> CANCELLED
 * SERVING -> INTERRUPTED (System crash/reboot pending Administrator triage)
 */
public enum TokenStatus {
    WAITING("Waiting in Queue"),
    SERVING("Currently Being Served"),
    COMPLETED("Service Completed"),
    SKIPPED("Customer Absent / Skipped"),
    CANCELLED("Cancelled by Customer"),
    INTERRUPTED("Service Interrupted (Pending Triage)");

    private final String description;

    TokenStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Determines whether this status represents a permanent terminal state.
     * 
     * Note: INTERRUPTED is strictly non-terminal because it requires administrative triage.
     * 
     * @return true if the token is COMPLETED, SKIPPED, or CANCELLED; false otherwise.
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == SKIPPED || this == CANCELLED;
    }

    @Override
    public String toString() {
        return description;
    }
}
