package com.badwallet.badwalletapi.dto.response;

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
public class WalletResponse {

    private Long id;
    private String phoneNumber;
    private String email;
    private BigDecimal balance;
    private String code;
    private String currency;
    private LocalDateTime createdAt;
}
