package com.pennywise.exception;

/** Base class for all PennyWise errors. The message is safe to show to the user. */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public AppException(String message) { super(message); }
    public AppException(String message, Throwable cause) { super(message, cause); }
}
