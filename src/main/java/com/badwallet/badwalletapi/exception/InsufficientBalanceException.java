package com.badwallet.badwalletapi.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BadWalletException {

    public InsufficientBalanceException() {
        super("Solde insuffisant pour effectuer cette opération", HttpStatus.BAD_REQUEST);
    }
}
