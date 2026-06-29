package com.badwallet.badwalletapi.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class PaymentServiceClientProxy implements PaymentServiceClient {

    private final PaymentServiceClientImpl paymentServiceClient;

    @Override
    public Map<String, Object> payBill(String billReference, BigDecimal amount, String walletPhoneNumber) {
        log.info("Proxy payment-service : paiement facture {} pour {} (montant: {})",
                billReference, walletPhoneNumber, amount);
        validatePaymentRequest(billReference, amount);
        Map<String, Object> result = paymentServiceClient.payBill(billReference, amount, walletPhoneNumber);
        log.info("Proxy payment-service : paiement facture {} terminé", billReference);
        return result;
    }

    @Override
    public Map<String, Object> payBillsByReferences(List<String> billReferences, String walletPhoneNumber) {
        log.info("Proxy payment-service : paiement de {} facture(s) pour {}",
                billReferences.size(), walletPhoneNumber);
        if (billReferences == null || billReferences.isEmpty()) {
            throw new IllegalArgumentException("La liste des références ne peut pas être vide");
        }
        Map<String, Object> result = paymentServiceClient.payBillsByReferences(billReferences, walletPhoneNumber);
        log.info("Proxy payment-service : paiement groupé terminé");
        return result;
    }

    private void validatePaymentRequest(String billReference, BigDecimal amount) {
        if (billReference == null || billReference.isBlank()) {
            throw new IllegalArgumentException("La référence de facture est obligatoire");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }
    }
}
