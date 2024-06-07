package com.bryanmzili.DevLab.exception;

public class MissingRequestBodyException extends RuntimeException {

    public MissingRequestBodyException(String message) {
        super(message);
    }
}
