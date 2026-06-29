package com.badwallet.badwalletapi.dto.response;

import com.badwallet.badwalletapi.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;
    private LocalDateTime createdAt;
    private String walletPhoneNumber;
}
