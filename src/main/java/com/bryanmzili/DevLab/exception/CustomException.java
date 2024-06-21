package com.bryanmzili.DevLab.exception;

public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
    
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
