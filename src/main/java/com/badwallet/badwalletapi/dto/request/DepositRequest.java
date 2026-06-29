package com.badwallet.badwalletapi.dto.request;

import com.badwallet.badwalletapi.entity.DepositMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à zéro")
    private BigDecimal amount;

    @NotNull(message = "La méthode de dépôt est obligatoire")
    private DepositMethod method;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String cardNumber;

    private String sourceWalletPhoneNumber;
}
