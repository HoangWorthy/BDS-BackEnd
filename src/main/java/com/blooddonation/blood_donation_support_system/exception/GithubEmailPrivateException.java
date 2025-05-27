package com.blooddonation.blood_donation_support_system.exception;

public class GithubEmailPrivateException extends RuntimeException {
    public GithubEmailPrivateException(String message) {

        super("Unable to access email. Please make your GitHub email public or use a different login method.");

    }
}
