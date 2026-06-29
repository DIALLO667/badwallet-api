package com.badwallet.badwalletapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {

    private TransactionResponse debitTransaction;
    private TransactionResponse creditTransaction;
    private String message;
}
