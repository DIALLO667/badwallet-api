package com.badwallet.badwalletapi.client;

import com.badwallet.badwalletapi.exception.PaymentServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentServiceClientImpl implements PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${payment.service.base-url}")
    private String paymentServiceBaseUrl;

    @Override
    public Map<String, Object> payBill(String billReference, BigDecimal amount, String walletPhoneNumber) {
        Map<String, Object> body = new HashMap<>();
        body.put("billReference", billReference);
        body.put("amount", amount);
        body.put("walletPhoneNumber", walletPhoneNumber);

        return executePost("/api/payments", body);
    }

    @Override
    public Map<String, Object> payBillsByReferences(List<String> billReferences, String walletPhoneNumber) {
        Map<String, Object> body = new HashMap<>();
        body.put("billReferences", billReferences);
        body.put("walletPhoneNumber", walletPhoneNumber);

        return executePost("/api/payments/references", body);
    }

    private Map<String, Object> executePost(String path, Map<String, Object> body) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    paymentServiceBaseUrl + path,
                    HttpMethod.POST,
                    new HttpEntity<>(body),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (RestClientException ex) {
            throw new PaymentServiceException("Erreur lors de l'appel au payment-service : " + ex.getMessage());
        }
    }
}
