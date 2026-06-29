package com.badwallet.badwalletapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentByReferencesRequest {

    @NotEmpty(message = "Au moins une référence de facture est requise")
    private List<@NotBlank(message = "La référence ne peut pas être vide") String> billReferences;

    @NotBlank(message = "La description est obligatoire")
    private String description;
}
