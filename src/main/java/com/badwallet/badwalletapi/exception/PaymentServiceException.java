package com.badwallet.badwalletapi.exception;

import org.springframework.http.HttpStatus;

public class PaymentServiceException extends BadWalletException {

    public PaymentServiceException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
