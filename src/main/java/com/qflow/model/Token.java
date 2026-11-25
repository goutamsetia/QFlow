package com.qflow.model;

import com.qflow.model.enums.Priority;
import com.qflow.model.enums.TokenStatus;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an issued queue service token in QFlow.
 * 
 * Tracks the lifecycle status, customer linkage, service department,
 * assigned counter station, and timestamp milestones.
 * 
 * Scheduling metadata (such as queue arrival sequence and starvation metrics)
 * are cleanly decoupled and managed by Member 2's queue scheduler.
 */
public class Token {

    private Long id;
    private String tokenNumber;
    private Long customerId;
    private Long serviceId;
    private Long counterId;
    private Priority priority;
    private TokenStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime servedAt;
    private LocalDateTime completedAt;

    /**
     * Default constructor for mapping/reflection.
     */
    public Token() {
        this.status = TokenStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Business constructor for new token creation.
     */
    public Token(String tokenNumber, Long customerId, Long serviceId, Priority priority) {
        this.tokenNumber = tokenNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.priority = priority;
        this.status = TokenStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Full constructor for rehydrating existing records from persistence.
     */
    public Token(Long id, String tokenNumber, Long customerId, Long serviceId, Long counterId,
                 Priority priority, TokenStatus status, LocalDateTime createdAt,
                 LocalDateTime servedAt, LocalDateTime completedAt) {
        this.id = id;
        this.tokenNumber = tokenNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.counterId = counterId;
        this.priority = priority;
        this.status = status != null ? status : TokenStatus.WAITING;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.servedAt = servedAt;
        this.completedAt = completedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getCounterId() {
        return counterId;
    }

    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getServedAt() {
        return servedAt;
    }

    public void setServedAt(LocalDateTime servedAt) {
        this.servedAt = servedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * Equality is based strictly on the unique business token number.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return Objects.equals(tokenNumber, token.tokenNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenNumber);
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", number='" + tokenNumber + '\'' +
                ", customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", counterId=" + counterId +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }
}
