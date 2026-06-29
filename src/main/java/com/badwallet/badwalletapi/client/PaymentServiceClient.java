package com.badwallet.badwalletapi.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentServiceClient {

    Map<String, Object> payBill(String billReference, BigDecimal amount, String walletPhoneNumber);

    Map<String, Object> payBillsByReferences(List<String> billReferences, String walletPhoneNumber);
}
