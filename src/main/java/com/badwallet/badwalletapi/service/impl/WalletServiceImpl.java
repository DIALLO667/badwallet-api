package com.badwallet.badwalletapi.service.impl;

import com.badwallet.badwalletapi.client.PaymentServiceClient;
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
import com.badwallet.badwalletapi.entity.Transaction;
import com.badwallet.badwalletapi.entity.TransactionType;
import com.badwallet.badwalletapi.entity.Wallet;
import com.badwallet.badwalletapi.exception.DuplicateWalletException;
import com.badwallet.badwalletapi.exception.InsufficientBalanceException;
import com.badwallet.badwalletapi.exception.WalletNotFoundException;
import com.badwallet.badwalletapi.repository.TransactionRepository;
import com.badwallet.badwalletapi.repository.WalletRepository;
import com.badwallet.badwalletapi.service.WalletService;
import com.badwallet.badwalletapi.service.strategy.DepositStrategy;
import com.badwallet.badwalletapi.service.strategy.DepositStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final BigDecimal WITHDRAW_FEE_RATE = new BigDecimal("0.01");
    private static final BigDecimal MAX_WITHDRAW_FEE = new BigDecimal("5000");

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final DepositStrategyFactory depositStrategyFactory;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    public WalletResponse createWallet(CreateWalletRequest request) {
        if (walletRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateWalletException("Un portefeuille existe déjà avec ce numéro de téléphone");
        }
        if (walletRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateWalletException("Un portefeuille existe déjà avec cet email");
        }

        Wallet wallet = Wallet.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .balance(request.getInitialBalance())
                .code(request.getCode())
                .currency(request.getCurrency())
                .build();

        return toWalletResponse(walletRepository.save(wallet));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> listWallets(Pageable pageable) {
        return walletRepository.findAll(pageable).map(this::toWalletResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getWalletByPhoneNumber(String phoneNumber) {
        return toWalletResponse(findWallet(phoneNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String phoneNumber) {
        Wallet wallet = findWallet(phoneNumber);
        return BalanceResponse.builder()
                .phoneNumber(wallet.getPhoneNumber())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }

    @Override
    public TransactionResponse deposit(String phoneNumber, DepositRequest request) {
        Wallet wallet = findWallet(phoneNumber);
        DepositStrategy strategy = depositStrategyFactory.getStrategy(request.getMethod());

        strategy.validate(request);
        strategy.process(wallet, request);

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description(request.getDescription())
                .build();

        wallet.addTransaction(transaction);
        walletRepository.save(wallet);

        return toTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse withdraw(String phoneNumber, WithdrawRequest request) {
        Wallet wallet = findWallet(phoneNumber);
        BigDecimal fee = calculateWithdrawFee(request.getAmount());
        BigDecimal totalDebit = request.getAmount().add(fee);

        if (wallet.getBalance().compareTo(totalDebit) < 0) {
            throw new InsufficientBalanceException();
        }

        wallet.setBalance(wallet.getBalance().subtract(totalDebit));

        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .fee(fee)
                .description(request.getDescription())
                .build();

        wallet.addTransaction(transaction);
        walletRepository.save(wallet);

        return toTransactionResponse(transaction);
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        if (request.getSourcePhoneNumber().equals(request.getTargetPhoneNumber())) {
            throw new IllegalArgumentException("Les portefeuilles source et cible doivent être différents");
        }

        Wallet source = findWallet(request.getSourcePhoneNumber());
        Wallet target = findWallet(request.getTargetPhoneNumber());

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        target.setBalance(target.getBalance().add(request.getAmount()));

        Transaction debit = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description("Transfert sortant vers " + target.getPhoneNumber() + " - " + request.getDescription())
                .build();

        Transaction credit = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description("Transfert entrant depuis " + source.getPhoneNumber() + " - " + request.getDescription())
                .build();

        source.addTransaction(debit);
        target.addTransaction(credit);

        walletRepository.save(source);
        walletRepository.save(target);

        return TransferResponse.builder()
                .debitTransaction(toTransactionResponse(debit))
                .creditTransaction(toTransactionResponse(credit))
                .message("Transfert effectué avec succès")
                .build();
    }

    @Override
    public PaymentResponse payBill(String phoneNumber, PaymentRequest request) {
        Wallet wallet = findWallet(phoneNumber);

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        Map<String, Object> paymentResult = paymentServiceClient.payBill(
                request.getBillReference(),
                request.getAmount(),
                phoneNumber
        );

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .type(TransactionType.PAYMENT)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description(request.getDescription() + " [ref: " + request.getBillReference() + "]")
                .build();

        wallet.addTransaction(transaction);
        walletRepository.save(wallet);

        return PaymentResponse.builder()
                .status(String.valueOf(paymentResult.getOrDefault("status", "SUCCESS")))
                .message(String.valueOf(paymentResult.getOrDefault("message", "Paiement effectué")))
                .totalAmount(request.getAmount())
                .billReferences(List.of(request.getBillReference()))
                .transaction(toTransactionResponse(transaction))
                .build();
    }

    @Override
    public PaymentResponse payBillsByReferences(String phoneNumber, PaymentByReferencesRequest request) {
        Wallet wallet = findWallet(phoneNumber);

        Map<String, Object> paymentResult = paymentServiceClient.payBillsByReferences(
                request.getBillReferences(),
                phoneNumber
        );

        BigDecimal totalAmount = extractTotalAmount(paymentResult, request.getBillReferences().size());

        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException();
        }

        wallet.setBalance(wallet.getBalance().subtract(totalAmount));

        Transaction transaction = Transaction.builder()
                .type(TransactionType.PAYMENT)
                .amount(totalAmount)
                .fee(BigDecimal.ZERO)
                .description(request.getDescription() + " [refs: " + String.join(", ", request.getBillReferences()) + "]")
                .build();

        wallet.addTransaction(transaction);
        walletRepository.save(wallet);

        return PaymentResponse.builder()
                .status(String.valueOf(paymentResult.getOrDefault("status", "SUCCESS")))
                .message(String.valueOf(paymentResult.getOrDefault("message", "Paiements effectués")))
                .totalAmount(totalAmount)
                .billReferences(request.getBillReferences())
                .transaction(toTransactionResponse(transaction))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionHistory(String phoneNumber, Pageable pageable) {
        Wallet wallet = findWallet(phoneNumber);
        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet, pageable)
                .map(this::toTransactionResponse);
    }

    private Wallet findWallet(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new WalletNotFoundException(phoneNumber));
    }

    private BigDecimal calculateWithdrawFee(BigDecimal amount) {
        BigDecimal fee = amount.multiply(WITHDRAW_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        return fee.min(MAX_WITHDRAW_FEE);
    }

    private BigDecimal extractTotalAmount(Map<String, Object> paymentResult, int referenceCount) {
        Object total = paymentResult.get("totalAmount");
        if (total instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (total instanceof String value) {
            return new BigDecimal(value);
        }
        return BigDecimal.valueOf(referenceCount * 1000L);
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .phoneNumber(wallet.getPhoneNumber())
                .email(wallet.getEmail())
                .balance(wallet.getBalance())
                .code(wallet.getCode())
                .currency(wallet.getCurrency())
                .createdAt(wallet.getCreatedAt())
                .build();
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .walletPhoneNumber(transaction.getWallet() != null ? transaction.getWallet().getPhoneNumber() : null)
                .build();
    }
}
