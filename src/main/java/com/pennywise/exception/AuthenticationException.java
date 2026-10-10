package com.pennywise.exception;

/** Thrown when a login attempt fails. */
public class AuthenticationException extends AppException {
    private static final long serialVersionUID = 1L;
    public AuthenticationException(String message) { super(message); }
}
