package com.blooddonation.blood_donation_support_system.enums;

public enum Status {
    PENDING("Pending"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CHECKED_IN("Checked In");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
