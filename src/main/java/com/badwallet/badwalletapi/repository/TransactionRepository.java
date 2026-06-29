package com.badwallet.badwalletapi.repository;

import com.badwallet.badwalletapi.entity.Transaction;
import com.badwallet.badwalletapi.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet, Pageable pageable);
}
