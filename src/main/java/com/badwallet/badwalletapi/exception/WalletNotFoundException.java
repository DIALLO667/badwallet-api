package com.badwallet.badwalletapi.exception;

import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends BadWalletException {

    public WalletNotFoundException(String phoneNumber) {
        super("Portefeuille introuvable pour le numéro : " + phoneNumber, HttpStatus.NOT_FOUND);
    }
}
