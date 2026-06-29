package com.badwallet.badwalletapi.service.strategy;

import com.badwallet.badwalletapi.dto.request.DepositRequest;
import com.badwallet.badwalletapi.entity.DepositMethod;
import com.badwallet.badwalletapi.entity.Wallet;

public interface DepositStrategy {

    DepositMethod supports();

    void validate(DepositRequest request);

    void process(Wallet wallet, DepositRequest request);
}
