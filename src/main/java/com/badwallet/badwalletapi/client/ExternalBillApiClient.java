package com.badwallet.badwalletapi.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalBillApiClient {

    private final RestTemplate restTemplate;

    @Value("${external.bills.base-url}")
    private String externalBillsBaseUrl;

    public ResponseEntity<String> proxyGet(String path) {
        String url = externalBillsBaseUrl + path;
        log.info("Proxy API factures externes : GET {}", url);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
        } catch (RestClientException ex) {
            log.error("Erreur proxy API factures : {}", ex.getMessage());
            throw ex;
        }
    }
}
