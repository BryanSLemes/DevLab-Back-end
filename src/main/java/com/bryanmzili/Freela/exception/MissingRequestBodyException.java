package com.bryanmzili.Freela.exception;

public class MissingRequestBodyException extends RuntimeException {

    public MissingRequestBodyException(String message) {
        super(message);
    }
}
