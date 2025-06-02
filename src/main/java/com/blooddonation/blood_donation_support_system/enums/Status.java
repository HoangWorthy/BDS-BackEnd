package com.blooddonation.blood_donation_support_system.enums;

public enum Status {
    PENDING("Pending"),
    CANCELED("Canceled"),
    COMPLETED("Completed"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
