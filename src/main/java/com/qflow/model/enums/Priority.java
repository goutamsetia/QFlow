package com.qflow.model.enums;

/**
 * Defines the priority tiers for queue scheduling in QFlow.
 * 
 * Lower numeric level values represent higher scheduling priority:
 * 1. {@link #EMERGENCY} (Highest - Level 1)
 * 2. {@link #SENIOR_CITIZEN} (Elevated - Level 2)
 * 3. {@link #NORMAL} (Standard - Level 3)
 */
public enum Priority {
    EMERGENCY(1, "Emergency"),
    SENIOR_CITIZEN(2, "Senior Citizen"),
    NORMAL(3, "Normal");

    private final int level;
    private final String displayName;

    Priority(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Helper to compare relative priority between two tiers.
     * 
     * @param other the other Priority to compare against
     * @return true if this priority level is strictly higher (lower integer level) than other
     */
    public boolean isHigherPriorityThan(Priority other) {
        if (other == null) return true;
        return this.level < other.level;
    }

    /**
     * Resolves a Priority enum by its numeric level.
     * 
     * @param level integer level (1, 2, or 3)
     * @return matching Priority enum
     * @throws IllegalArgumentException if no match found
     */
    public static Priority fromLevel(int level) {
        for (Priority p : values()) {
            if (p.level == level) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown priority level: " + level);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
