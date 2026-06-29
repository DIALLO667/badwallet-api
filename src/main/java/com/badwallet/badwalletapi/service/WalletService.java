package com.badwallet.badwalletapi.service;

import com.badwallet.badwalletapi.dto.request.CreateWalletRequest;
import com.badwallet.badwalletapi.dto.request.DepositRequest;
import com.badwallet.badwalletapi.dto.request.PaymentByReferencesRequest;
import com.badwallet.badwalletapi.dto.request.PaymentRequest;
import com.badwallet.badwalletapi.dto.request.TransferRequest;
import com.badwallet.badwalletapi.dto.request.WithdrawRequest;
import com.badwallet.badwalletapi.dto.response.BalanceResponse;
import com.badwallet.badwalletapi.dto.response.PaymentResponse;
import com.badwallet.badwalletapi.dto.response.TransactionResponse;
import com.badwallet.badwalletapi.dto.response.TransferResponse;
import com.badwallet.badwalletapi.dto.response.WalletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletService {

    WalletResponse createWallet(CreateWalletRequest request);

    Page<WalletResponse> listWallets(Pageable pageable);

    WalletResponse getWalletByPhoneNumber(String phoneNumber);

    BalanceResponse getBalance(String phoneNumber);

    TransactionResponse deposit(String phoneNumber, DepositRequest request);

    TransactionResponse withdraw(String phoneNumber, WithdrawRequest request);

    TransferResponse transfer(TransferRequest request);

    PaymentResponse payBill(String phoneNumber, PaymentRequest request);

    PaymentResponse payBillsByReferences(String phoneNumber, PaymentByReferencesRequest request);

    Page<TransactionResponse> getTransactionHistory(String phoneNumber, Pageable pageable);
}
