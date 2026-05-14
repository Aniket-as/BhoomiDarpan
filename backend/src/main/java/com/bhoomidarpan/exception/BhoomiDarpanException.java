package com.bhoomidarpan.exception;

public class BhoomiDarpanException extends RuntimeException {

    public BhoomiDarpanException(String message) {
        super(message);
    }

    public BhoomiDarpanException(String message, Throwable cause) {
        super(message, cause);
    }
}