package com.badwallet.badwalletapi.service.strategy;

import com.badwallet.badwalletapi.entity.DepositMethod;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class DepositStrategyFactory {

    private final Map<DepositMethod, DepositStrategy> strategies = new EnumMap<>(DepositMethod.class);

    public DepositStrategyFactory(List<DepositStrategy> depositStrategies) {
        depositStrategies.forEach(strategy -> strategies.put(strategy.supports(), strategy));
    }

    public DepositStrategy getStrategy(DepositMethod method) {
        DepositStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Méthode de dépôt non supportée : " + method);
        }
        return strategy;
    }
}
