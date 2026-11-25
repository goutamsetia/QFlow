package com.qflow.model.dto;

import com.qflow.model.enums.Priority;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object containing formatted details printed on a customer's token slip.
 */
public class TokenSlipDTO {

    private final String tokenNumber;
    private final String customerName;
    private final String serviceName;
    private final Priority priority;
    private final int queuePositionAhead;
    private final int estimatedWaitMinutes;
    private final LocalDateTime issuedAt;

    public TokenSlipDTO(String tokenNumber, String customerName, String serviceName,
                        Priority priority, int queuePositionAhead, int estimatedWaitMinutes,
                        LocalDateTime issuedAt) {
        this.tokenNumber = tokenNumber;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.priority = priority;
        this.queuePositionAhead = queuePositionAhead;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.issuedAt = issuedAt != null ? issuedAt : LocalDateTime.now();
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getQueuePositionAhead() {
        return queuePositionAhead;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    /**
     * Formats the token slip as a clean ASCII slip for console display.
     */
    public String toFormattedSlip() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "========================================%n" +
            "               QFLOW TOKEN              %n" +
            "========================================%n" +
            " Token Number : %s%n" +
            " Customer     : %s%n" +
            " Service      : %s%n" +
            " Priority     : %s%n" +
            " Queue Ahead  : %d customer(s)%n" +
            " Est. Wait    : ~%d minutes%n" +
            " Issued At    : %s%n" +
            "========================================",
            tokenNumber, customerName, serviceName, priority.getDisplayName(),
            queuePositionAhead, estimatedWaitMinutes, issuedAt.format(dtf)
        );
    }

    @Override
    public String toString() {
        return toFormattedSlip();
    }
}
