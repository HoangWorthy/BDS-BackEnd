package com.blooddonation.blood_donation_support_system.enums;

public enum Status {
    PENDING("Pending"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CHECKED_IN("Checked In"),
    ENABLE("Enable"),
    DISABLE("Disable"),
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable");



    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
