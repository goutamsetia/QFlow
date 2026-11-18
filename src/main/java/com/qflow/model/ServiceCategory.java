package com.qflow.model;

/**
 * Domain model representing a service department in QFlow.
 * 
 * Examples:
 * - REG: Registrar & Transcripts (avg 12 min)
 * - ACC: Accounts & Fee Payments (avg 8 min)
 * - ADM: Admissions & Verification (avg 15 min)
 * - EXM: Examinations & Grade Cards (avg 10 min)
 */
public class ServiceCategory {

    private Long id;
    private String serviceCode;
    private String serviceName;
    private int avgDurationMinutes;
    private boolean active;

    public ServiceCategory() {
        this.active = true;
    }

    public ServiceCategory(Long id, String serviceCode, String serviceName, int avgDurationMinutes) {
        this.id = id;
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.avgDurationMinutes = avgDurationMinutes;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getAvgDurationMinutes() {
        return avgDurationMinutes;
    }

    public void setAvgDurationMinutes(int avgDurationMinutes) {
        this.avgDurationMinutes = avgDurationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "ServiceCategory{" +
                "id=" + id +
                ", code='" + serviceCode + '\'' +
                ", name='" + serviceName + '\'' +
                ", avgDurationMinutes=" + avgDurationMinutes +
                '}';
    }
}
