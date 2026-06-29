package com.badwallet.badwalletapi.controller;

import com.badwallet.badwalletapi.client.ExternalBillApiClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillProxyController {

    private final ExternalBillApiClient externalBillApiClient;

    @GetMapping("/**")
    public ResponseEntity<String> proxyBills(HttpServletRequest request) {
        String path = request.getRequestURI().replace("/api/bills", "");
        if (path.isEmpty()) {
            path = "/";
        }
        return externalBillApiClient.proxyGet(path);
    }
}
