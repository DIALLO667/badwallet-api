package com.badwallet.badwalletapi.exception;

import org.springframework.http.HttpStatus;

public class DuplicateWalletException extends BadWalletException {

    public DuplicateWalletException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
