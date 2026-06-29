package com.badwallet.badwalletapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadWalletException extends RuntimeException {

    private final HttpStatus status;

    public BadWalletException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
