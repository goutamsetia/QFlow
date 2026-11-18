package com.qflow.model.dto;

import com.qflow.model.enums.TokenStatus;

/**
 * Data Transfer Object returned when a customer or operator queries a token's current status.
 */
public class TokenStatusDTO {

    private final String tokenNumber;
    private final TokenStatus status;
    private final Integer counterNumber;       // Nullable if still waiting or cancelled
    private final String operatorName;         // Nullable if still waiting or cancelled
    private final int queuePositionAhead;      // 0 if currently serving or completed
    private final int estimatedWaitMinutes;

    public TokenStatusDTO(String tokenNumber, TokenStatus status, Integer counterNumber,
                          String operatorName, int queuePositionAhead, int estimatedWaitMinutes) {
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.counterNumber = counterNumber;
        this.operatorName = operatorName;
        this.queuePositionAhead = queuePositionAhead;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public Integer getCounterNumber() {
        return counterNumber;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public int getQueuePositionAhead() {
        return queuePositionAhead;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    @Override
    public String toString() {
        return "TokenStatusDTO{" +
                "tokenNumber='" + tokenNumber + '\'' +
                ", status=" + status +
                ", counterNumber=" + counterNumber +
                ", operatorName='" + operatorName + '\'' +
                ", queuePositionAhead=" + queuePositionAhead +
                ", estimatedWaitMinutes=" + estimatedWaitMinutes +
                '}';
    }
}
