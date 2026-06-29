package com.badwallet.badwalletapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String status;
    private String message;
    private BigDecimal totalAmount;
    private List<String> billReferences;
    private TransactionResponse transaction;
}
