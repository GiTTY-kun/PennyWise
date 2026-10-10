package com.pennywise.exception;

/** Thrown when user input is missing or invalid (empty name, negative amount, ...). */
public class ValidationException extends AppException {
    private static final long serialVersionUID = 1L;
    public ValidationException(String message) { super(message); }
}
