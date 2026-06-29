package com.badwallet.badwalletapi.controller;

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
import com.badwallet.badwalletapi.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(request));
    }

    @GetMapping
    public ResponseEntity<Page<WalletResponse>> listWallets(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(walletService.listWallets(pageable));
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getWalletByPhoneNumber(phoneNumber));
    }

    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getBalance(phoneNumber));
    }

    @PostMapping("/{phoneNumber}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String phoneNumber,
            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(phoneNumber, request));
    }

    @PostMapping("/{phoneNumber}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String phoneNumber,
            @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdraw(phoneNumber, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(request));
    }

    @PostMapping("/{phoneNumber}/payments")
    public ResponseEntity<PaymentResponse> payBill(
            @PathVariable String phoneNumber,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(walletService.payBill(phoneNumber, request));
    }

    @PostMapping("/{phoneNumber}/payments/references")
    public ResponseEntity<PaymentResponse> payBillsByReferences(
            @PathVariable String phoneNumber,
            @Valid @RequestBody PaymentByReferencesRequest request) {
        return ResponseEntity.ok(walletService.payBillsByReferences(phoneNumber, request));
    }

    @GetMapping("/{phoneNumber}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(
            @PathVariable String phoneNumber,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(walletService.getTransactionHistory(phoneNumber, pageable));
    }
}
