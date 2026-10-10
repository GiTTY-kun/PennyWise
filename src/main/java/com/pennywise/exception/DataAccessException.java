package com.pennywise.exception;

/** Wraps a checked SQLException so the rest of the code does not depend on JDBC. */
public class DataAccessException extends AppException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
