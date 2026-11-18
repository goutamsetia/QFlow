package com.qflow.model.dto;

/**
 * Data Transfer Object for requesting token issuance.
 * 
 * Enforces security boundaries: customer self-service kiosks cannot self-assign
 * emergency priority. Emergency priority requires staff override authorization.
 */
public class TokenRequestDTO {

    private final Long customerId;
    private final Long serviceId;
    private final boolean emergencyAuthorized;
    private final String staffAuthCode;

    /**
     * Factory method for customer self-service kiosk.
     * Guaranteed to never enable emergency priority authorization.
     */
    public static TokenRequestDTO forKiosk(Long customerId, Long serviceId) {
        return new TokenRequestDTO(customerId, serviceId, false, null);
    }

    /**
     * Factory method for counter staff / admin override.
     * Allows emergency escalation with a required authorization code.
     */
    public static TokenRequestDTO forStaffOverride(Long customerId, Long serviceId, String staffAuthCode) {
        return new TokenRequestDTO(customerId, serviceId, true, staffAuthCode);
    }

    public TokenRequestDTO(Long customerId, Long serviceId, boolean emergencyAuthorized, String staffAuthCode) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.emergencyAuthorized = emergencyAuthorized;
        this.staffAuthCode = (staffAuthCode != null && !staffAuthCode.trim().isEmpty()) 
                ? staffAuthCode.trim() 
                : null;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public boolean isEmergencyAuthorized() {
        return emergencyAuthorized;
    }

    public String getStaffAuthCode() {
        return staffAuthCode;
    }

    @Override
    public String toString() {
        return "TokenRequestDTO{" +
                "customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", emergencyAuthorized=" + emergencyAuthorized +
                '}';
    }
}
