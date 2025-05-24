package com.blooddonation.blood_donation_support_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // If user is not logged in and tries to access a protected resource, redirect to login page
    @ExceptionHandler(org.springframework.web.bind.MissingRequestCookieException.class)
    protected void handleMissingRequestCookie(org.springframework.web.bind.MissingRequestCookieException ex, HttpServletResponse response) throws IOException {
        response.sendRedirect("/login");
    }

    //this need review, not sure if this is the right way to handle this exception
    @ExceptionHandler(OAuth2AttributeException.class)
    public ResponseEntity<Object> handleOAuth2AttributeException(OAuth2AttributeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}