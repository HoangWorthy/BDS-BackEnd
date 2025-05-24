package com.blooddonation.blood_donation_support_system.exception;

public class MissingRequestCookieException extends RuntimeException {
    public MissingRequestCookieException(String message) {
        super("you are not authorized to access this resource. " + message);
    }
}
