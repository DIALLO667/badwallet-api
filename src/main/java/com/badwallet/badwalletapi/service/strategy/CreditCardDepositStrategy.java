package com.badwallet.badwalletapi.service.strategy;

import com.badwallet.badwalletapi.dto.request.DepositRequest;
import com.badwallet.badwalletapi.entity.DepositMethod;
import com.badwallet.badwalletapi.entity.Wallet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CreditCardDepositStrategy implements DepositStrategy {

    @Override
    public DepositMethod supports() {
        return DepositMethod.CREDIT_CARD;
    }

    @Override
    public void validate(DepositRequest request) {
        if (!StringUtils.hasText(request.getCardNumber())) {
            throw new IllegalArgumentException("Le numéro de carte est obligatoire pour un dépôt par carte");
        }
        if (request.getCardNumber().length() < 12) {
            throw new IllegalArgumentException("Le numéro de carte est invalide");
        }
    }

    @Override
    public void process(Wallet wallet, DepositRequest request) {
        // Simulation du traitement carte bancaire
    }
}
