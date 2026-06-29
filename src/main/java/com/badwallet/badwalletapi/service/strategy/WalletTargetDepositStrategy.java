package com.badwallet.badwalletapi.service.strategy;

import com.badwallet.badwalletapi.dto.request.DepositRequest;
import com.badwallet.badwalletapi.entity.DepositMethod;
import com.badwallet.badwalletapi.entity.Wallet;
import com.badwallet.badwalletapi.exception.InsufficientBalanceException;
import com.badwallet.badwalletapi.exception.WalletNotFoundException;
import com.badwallet.badwalletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class WalletTargetDepositStrategy implements DepositStrategy {

    private final WalletRepository walletRepository;

    @Override
    public DepositMethod supports() {
        return DepositMethod.WALLET_TARGET;
    }

    @Override
    public void validate(DepositRequest request) {
        if (!StringUtils.hasText(request.getSourceWalletPhoneNumber())) {
            throw new IllegalArgumentException("Le portefeuille source est obligatoire pour WALLET_TARGET");
        }
    }

    @Override
    public void process(Wallet targetWallet, DepositRequest request) {
        Wallet sourceWallet = walletRepository.findByPhoneNumber(request.getSourceWalletPhoneNumber())
                .orElseThrow(() -> new WalletNotFoundException(request.getSourceWalletPhoneNumber()));

        if (sourceWallet.getId().equals(targetWallet.getId())) {
            throw new IllegalArgumentException("Le portefeuille source et cible doivent être différents");
        }

        BigDecimal amount = request.getAmount();
        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        walletRepository.save(sourceWallet);
    }
}
