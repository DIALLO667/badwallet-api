package com.badwallet.badwalletapi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalletRequest {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String phoneNumber;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotNull(message = "Le solde initial est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le solde initial doit être positif ou nul")
    private BigDecimal initialBalance;

    @NotBlank(message = "Le code est obligatoire")
    @Size(min = 4, max = 10, message = "Le code doit contenir entre 4 et 10 caractères")
    private String code;

    @NotBlank(message = "La devise est obligatoire")
    @Size(min = 3, max = 5, message = "La devise doit contenir entre 3 et 5 caractères")
    private String currency;
}
