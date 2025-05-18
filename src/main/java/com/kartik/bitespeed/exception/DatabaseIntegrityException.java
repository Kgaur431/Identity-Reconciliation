package com.kartik.bitespeed.exception;

public class DatabaseIntegrityException extends RuntimeException {

    public DatabaseIntegrityException(String message) {
        super(message);
    }

    public DatabaseIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
