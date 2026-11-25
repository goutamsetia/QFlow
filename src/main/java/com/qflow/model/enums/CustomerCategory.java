package com.qflow.model.enums;

/**
 * Defines demographic categories for registered customers in QFlow.
 * 
 * Used for customer classification, priority inference, and institutional reporting.
 */
public enum CustomerCategory {
    STUDENT("Student"),
    STAFF("Faculty / Staff"),
    SENIOR_CITIZEN("Senior Citizen"),
    GUEST("Visitor / Guest");

    private final String displayName;

    CustomerCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
